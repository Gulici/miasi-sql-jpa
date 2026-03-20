package pwr.miasi.parser;

import pwr.miasi.antlr4.SqlBaseVisitor;
import pwr.miasi.antlr4.SqlParser;
import pwr.miasi.model.Column;
import pwr.miasi.model.Table;

import java.util.ArrayList;
import java.util.List;

public class SchemaVisitor extends SqlBaseVisitor<Void> {

    private final List<Table> tables = new ArrayList<>();

    public List<Table> getTables() {
        return tables;
    }

    @Override
    public Void visitCreateTable(SqlParser.CreateTableContext ctx) {
        String tableName = ctx.tableName().getText();
        Table table = new Table(tableName);

        for (SqlParser.ColumnDefContext colCtx : ctx.columnDef()) {
            String colName = colCtx.columnName().getText();
            String sqlType = colCtx.type().getText();

            // Mapowanie typów SQL → Java
            String javaType = mapType(sqlType);

            Column column = new Column(colName, javaType);

            // Constrainty
            for (SqlParser.ColumnConstraintContext constraintCtx : colCtx.columnConstraint()) {
                if (constraintCtx.PRIMARY() != null) {
                    column.setId(true);
                    column.setNullable(false); // PK nie może być NULL
                }
                if (constraintCtx.NOT() != null) {
                    column.setNullable(false);
                }
                if (constraintCtx.UNIQUE() != null) {
                    column.setUnique(true);
                }
            }

            table.addColumn(column);
        }

        tables.add(table);
        return null;
    }

    // Proste mapowanie typów SQL na Java
    private String mapType(String sqlType) {
        sqlType = sqlType.toUpperCase();
        if (sqlType.startsWith("VARCHAR")) return "String";
        return switch (sqlType) {
            case "INT" -> "Integer";
            case "BIGINT" -> "Long";
            case "TEXT" -> "String";
            case "TIMESTAMP" -> "java.time.LocalDateTime";
            default -> "String";
        };
    }

}
