package pwr.miasi.entity;

public class RelationField {
    private final RelationType type;
    private final String fieldName;
    private final String targetEntity;
    private final boolean owning;
    private final String mappedBy;
    private final String joinColumn;
    private final String joinTableName;
    private final String inverseJoinColumn;

    public RelationField(
            RelationType type,
            String fieldName,
            String targetEntity,
            boolean owning,
            String mappedBy,
            String joinColumn,
            String joinTableName,
            String inverseJoinColumn
    ) {
        this.type = type;
        this.fieldName = fieldName;
        this.targetEntity = targetEntity;
        this.owning = owning;
        this.mappedBy = mappedBy;
        this.joinColumn = joinColumn;
        this.joinTableName = joinTableName;
        this.inverseJoinColumn = inverseJoinColumn;
    }

    public RelationType getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public boolean isOwning() {
        return owning;
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public String getInverseJoinColumn() {
        return inverseJoinColumn;
    }

    public boolean isManyToOne() {
        return type == RelationType.MANY_TO_ONE;
    }

    public boolean isOneToMany() {
        return type == RelationType.ONE_TO_MANY;
    }

    public boolean isOneToOne() {
        return type == RelationType.ONE_TO_ONE;
    }

    public boolean isManyToMany() {
        return type == RelationType.MANY_TO_MANY;
    }
}
