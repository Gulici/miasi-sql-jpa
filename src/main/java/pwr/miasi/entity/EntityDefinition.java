package pwr.miasi.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EntityDefinition {
    private final String tableName;
    private final String className;
    private final List<ScalarField> scalarFields = new ArrayList<>();
    private final List<RelationField> relationFields = new ArrayList<>();

    public EntityDefinition(String tableName, String className) {
        this.tableName = tableName;
        this.className = className;
    }

    public String getTableName() {
        return tableName;
    }

    public String getClassName() {
        return className;
    }

    public List<ScalarField> getScalarFields() {
        return Collections.unmodifiableList(scalarFields);
    }

    public List<RelationField> getRelationFields() {
        return Collections.unmodifiableList(relationFields);
    }

    public void addScalarField(ScalarField field) {
        scalarFields.add(field);
    }

    public void addRelationField(RelationField relationField) {
        boolean exists = relationFields.stream().anyMatch(existing ->
                existing.getFieldName().equalsIgnoreCase(relationField.getFieldName())
                        && existing.getType() == relationField.getType()
                        && existing.getTargetEntity().equals(relationField.getTargetEntity())
        );
        if (!exists) {
            relationFields.add(relationField);
        }
    }

    public void removeScalarFieldByColumnName(String columnName) {
        for (Iterator<ScalarField> iterator = scalarFields.iterator(); iterator.hasNext(); ) {
            ScalarField field = iterator.next();
            if (field.getColumnName().equalsIgnoreCase(columnName)) {
                iterator.remove();
            }
        }
    }
}
