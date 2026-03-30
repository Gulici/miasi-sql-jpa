package pwr.miasi.parser;

import pwr.miasi.antlr4.SqlBaseVisitor;
import pwr.miasi.antlr4.SqlParser;
import pwr.miasi.model.Column;
import pwr.miasi.model.ForeignKey;
import pwr.miasi.model.PrimaryKey;
import pwr.miasi.model.SchemaModel;
import pwr.miasi.model.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SchemaVisitor extends SqlBaseVisitor<Void> {
    private final SchemaModel schemaModel = new SchemaModel();

    /** Returns the schema built from parsed SQL statements. */
    public SchemaModel getSchemaModel() {
        return schemaModel;
    }

    @Override
    public Void visitSchema(SqlParser.SchemaContext ctx) {
        // Visit all CREATE TABLE statements first, then validate FK targets globally.
        Void result = super.visitSchema(ctx);
        validateForeignKeyTargets();
        return result;
    }

    @Override
    public Void visitCreateTable(SqlParser.CreateTableContext ctx) {
        // Build one table definition from a single CREATE TABLE statement.
        String tableName = ctx.tableName().getText();
        Table table = new Table(tableName);

        List<ForeignKey> inlineForeignKeys = new ArrayList<>();
        Set<String> inlinePrimaryKeyColumns = new LinkedHashSet<>();
        for (SqlParser.TableElementContext elementCtx : ctx.tableElement()) {
            if (elementCtx.columnDef() != null) {
                SqlParser.ColumnDefContext colCtx = elementCtx.columnDef();
                String colName = colCtx.columnName().getText();
                String sqlType = colCtx.dataType().getText();
                String javaType = SqlTypeMapper.toJavaType(sqlType);
                Column column = new Column(colName, sqlType, javaType);

                // Store VARCHAR length, e.g. VARCHAR(100) -> length = 100.
                if (colCtx.dataType().VARCHAR() != null) {
                    column.setLength(Integer.parseInt(colCtx.dataType().NUMBER().getText()));
                }

                for (SqlParser.ColumnConstraintContext constraintCtx : colCtx.columnConstraint()) {
                    if (constraintCtx.PRIMARY() != null) {
                        column.setPrimaryKeyPart(true);
                        column.setNullable(false);
                        inlinePrimaryKeyColumns.add(column.getName());
                    }
                    if (constraintCtx.NOT() != null) {
                        column.setNullable(false);
                    }
                    if (constraintCtx.UNIQUE() != null) {
                        column.setUnique(true);
                    }
                    if (constraintCtx.referencesClause() != null) {
                        SqlParser.ReferencesClauseContext referencesCtx = constraintCtx.referencesClause();
                        inlineForeignKeys.add(new ForeignKey(
                                null,
                                Collections.singletonList(column.getName()),
                                referencesCtx.tableName().getText(),
                                parseColumnNameList(referencesCtx.columnNameList())
                        ));
                    }
                }

                table.addColumn(column);
            } else if (elementCtx.tableConstraint() != null) {
                SqlParser.TableConstraintContext constraintCtx = elementCtx.tableConstraint();
                String constraintName = constraintCtx.constraintName() != null
                        ? constraintCtx.constraintName().getText()
                        : null;

                if (constraintCtx.PRIMARY() != null) {
                    // Table-level PK: PRIMARY KEY(col1, col2, ...)
                    List<String> pkColumns = parseColumnNameList(constraintCtx.columnNameList());
                    for (String pkColumnName : pkColumns) {
                        Column column = table.getColumnByName(pkColumnName);
                        if (column != null) {
                            column.setPrimaryKeyPart(true);
                            column.setNullable(false);
                        }
                    }
                    table.setPrimaryKey(new PrimaryKey(pkColumns));
                } else if (constraintCtx.FOREIGN() != null) {
                    // Table-level FK: FOREIGN KEY(...) REFERENCES ...
                    List<String> localColumns = parseColumnNameList(constraintCtx.columnNameList());
                    SqlParser.ReferencesClauseContext referencesCtx = constraintCtx.referencesClause();
                    ForeignKey fk = new ForeignKey(
                            constraintName,
                            localColumns,
                            referencesCtx.tableName().getText(),
                            parseColumnNameList(referencesCtx.columnNameList())
                    );
                    table.addForeignKey(fk);
                } else if (constraintCtx.UNIQUE() != null) {
                    // Table-level UNIQUE over one or more columns.
                    List<String> uniqueColumns = parseColumnNameList(constraintCtx.columnNameList());
                    for (String uniqueColumnName : uniqueColumns) {
                        Column column = table.getColumnByName(uniqueColumnName);
                        if (column != null) {
                            column.setUnique(true);
                        }
                    }
                }
            }
        }

        if (!inlinePrimaryKeyColumns.isEmpty() && table.getPrimaryKey().isEmpty()) {
            table.setPrimaryKey(new PrimaryKey(new ArrayList<>(inlinePrimaryKeyColumns)));
        }
        for (ForeignKey inlineForeignKey : inlineForeignKeys) {
            table.addForeignKey(inlineForeignKey);
        }

        validateTable(table);
        schemaModel.addTable(table);
        return null;
    }

    private List<String> parseColumnNameList(SqlParser.ColumnNameListContext ctx) {
        // Helper: parser context -> plain list of column names.
        List<String> columns = new ArrayList<>();
        for (SqlParser.ColumnNameContext colCtx : ctx.columnName()) {
            columns.add(colCtx.getText());
        }
        return columns;
    }

    private void validateTable(Table table) {
        // Ensure FK local columns exist inside current table.
        for (ForeignKey fk : table.getForeignKeys()) {
            for (String localColumn : fk.getLocalColumnNames()) {
                if (table.getColumnByName(localColumn) == null) {
                    throw new IllegalStateException(
                            "Foreign key in table '" + table.getName() + "' references missing local column '" + localColumn + "'"
                    );
                }
            }
        }
    }

    private void validateForeignKeyTargets() {
        // Ensure FK target table and target columns exist in schema.
        for (Table table : schemaModel.getTables()) {
            for (ForeignKey fk : table.getForeignKeys()) {
                Table referencedTable = schemaModel.getTableByName(fk.getReferencedTable());
                if (referencedTable == null) {
                    throw new IllegalStateException(
                            "Foreign key in table '" + table.getName() + "' references missing table '" + fk.getReferencedTable() + "'"
                    );
                }
                for (String referencedColumn : fk.getReferencedColumnNames()) {
                    if (referencedTable.getColumnByName(referencedColumn) == null) {
                        throw new IllegalStateException(
                                "Foreign key in table '" + table.getName() + "' references missing column '" + referencedColumn
                                        + "' in table '" + referencedTable.getName() + "'"
                        );
                    }
                }
            }
        }
    }
}
