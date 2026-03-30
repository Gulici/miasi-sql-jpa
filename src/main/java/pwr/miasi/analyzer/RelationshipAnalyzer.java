package pwr.miasi.analyzer;

import pwr.miasi.entity.EntityDefinition;
import pwr.miasi.entity.EntityModel;
import pwr.miasi.entity.RelationField;
import pwr.miasi.entity.RelationType;
import pwr.miasi.entity.ScalarField;
import pwr.miasi.model.Column;
import pwr.miasi.model.ForeignKey;
import pwr.miasi.model.SchemaModel;
import pwr.miasi.model.Table;
import pwr.miasi.util.NamingUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RelationshipAnalyzer {

    /**
     * Converts raw schema model into entity model with inferred JPA relations.
     */
    public EntityModel analyze(SchemaModel schemaModel) {
        // Join tables are used to build ManyToMany and skipped as standalone entities.
        Set<String> joinTableNames = detectJoinTables(schemaModel);
        EntityModel entityModel = new EntityModel();

        for (Table table : schemaModel.getTables()) {
            if (joinTableNames.contains(table.getName().toLowerCase())) {
                continue;
            }
            EntityDefinition entity = new EntityDefinition(table.getName(), NamingUtils.toClassName(table.getName()));
            for (Column column : table.getColumns()) {
                boolean isId = table.getPrimaryKey()
                        .map(pk -> pk.getColumnNames().stream().anyMatch(pkCol -> pkCol.equalsIgnoreCase(column.getName())))
                        .orElse(column.isPrimaryKeyPart());
                entity.addScalarField(new ScalarField(
                        NamingUtils.toFieldName(column.getName()),
                        column.getJavaType(),
                        column.getName(),
                        isId,
                        column.isNullable(),
                        column.isUnique(),
                        column.getLength()
                ));
            }
            entityModel.addEntity(entity);
        }

        for (Table table : schemaModel.getTables()) {
            if (joinTableNames.contains(table.getName().toLowerCase())) {
                continue;
            }
            for (ForeignKey fk : table.getForeignKeys()) {
                EntityDefinition source = entityModel.getByTableName(table.getName());
                EntityDefinition target = entityModel.getByTableName(fk.getReferencedTable());
                if (source == null || target == null) {
                    continue;
                }
                // FK side is owning side: ManyToOne or OneToOne.
                boolean oneToOne = isOneToOne(table, fk);
                String owningFieldName = NamingUtils.toFieldName(target.getClassName());
                String inverseFieldName = oneToOne
                        ? NamingUtils.toFieldName(source.getClassName())
                        : NamingUtils.pluralize(NamingUtils.toFieldName(source.getClassName()));

                source.addRelationField(new RelationField(
                        oneToOne ? RelationType.ONE_TO_ONE : RelationType.MANY_TO_ONE,
                        owningFieldName,
                        target.getClassName(),
                        true,
                        null,
                        fk.getLocalColumnNames().get(0),
                        null,
                        null
                ));
                for (String localColumn : fk.getLocalColumnNames()) {
                    // Replace raw FK column with relation field on entity.
                    source.removeScalarFieldByColumnName(localColumn);
                }

                // Add inverse side: OneToMany or OneToOne(mappedBy=...).
                target.addRelationField(new RelationField(
                        oneToOne ? RelationType.ONE_TO_ONE : RelationType.ONE_TO_MANY,
                        inverseFieldName,
                        source.getClassName(),
                        false,
                        owningFieldName,
                        null,
                        null,
                        null
                ));
            }
        }

        for (Table joinTable : schemaModel.getTables()) {
            if (!joinTableNames.contains(joinTable.getName().toLowerCase())) {
                continue;
            }
            List<ForeignKey> fks = joinTable.getForeignKeys();
            ForeignKey leftFk = fks.get(0);
            ForeignKey rightFk = fks.get(1);
            EntityDefinition left = entityModel.getByTableName(leftFk.getReferencedTable());
            EntityDefinition right = entityModel.getByTableName(rightFk.getReferencedTable());
            if (left == null || right == null) {
                continue;
            }

            String leftFieldName = NamingUtils.pluralize(NamingUtils.toFieldName(right.getClassName()));
            String rightFieldName = NamingUtils.pluralize(NamingUtils.toFieldName(left.getClassName()));

            // For detected join tables create bidirectional ManyToMany relation.
            left.addRelationField(new RelationField(
                    RelationType.MANY_TO_MANY,
                    leftFieldName,
                    right.getClassName(),
                    true,
                    null,
                    leftFk.getLocalColumnNames().get(0),
                    joinTable.getName(),
                    rightFk.getLocalColumnNames().get(0)
            ));

            right.addRelationField(new RelationField(
                    RelationType.MANY_TO_MANY,
                    rightFieldName,
                    left.getClassName(),
                    false,
                    leftFieldName,
                    null,
                    null,
                    null
            ));
        }

        return entityModel;
    }

    private Set<String> detectJoinTables(SchemaModel schemaModel) {
        // Heuristic: exactly 2 foreign keys and no extra business columns.
        Set<String> joinTables = new HashSet<>();
        for (Table table : schemaModel.getTables()) {
            if (table.getForeignKeys().size() != 2) {
                continue;
            }
            Set<String> fkColumns = new HashSet<>();
            for (ForeignKey fk : table.getForeignKeys()) {
                fkColumns.addAll(fk.getLocalColumnNames());
            }
            boolean onlyFkColumns = table.getColumns().stream()
                    .allMatch(column -> fkColumns.contains(column.getName()));
            if (onlyFkColumns) {
                joinTables.add(table.getName().toLowerCase());
            }
        }
        return joinTables;
    }

    private boolean isOneToOne(Table table, ForeignKey fk) {
        // OneToOne if FK column is unique or is the table primary key.
        if (fk.getLocalColumnNames().size() != 1) {
            return false;
        }
        String localColumnName = fk.getLocalColumnNames().get(0);
        Column column = table.getColumnByName(localColumnName);
        if (column == null) {
            return false;
        }
        boolean isPk = table.getPrimaryKey()
                .map(pk -> pk.getColumnNames().size() == 1
                        && pk.getColumnNames().get(0).equalsIgnoreCase(localColumnName))
                .orElse(false);
        return isPk || column.isUnique();
    }
}
