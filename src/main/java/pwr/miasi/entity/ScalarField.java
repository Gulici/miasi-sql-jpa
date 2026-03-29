package pwr.miasi.entity;

public class ScalarField {
    private final String name;
    private final String type;
    private final String columnName;
    private final boolean id;
    private final boolean nullable;
    private final boolean unique;
    private final Integer length;

    public ScalarField(String name, String type, String columnName, boolean id, boolean nullable, boolean unique, Integer length) {
        this.name = name;
        this.type = type;
        this.columnName = columnName;
        this.id = id;
        this.nullable = nullable;
        this.unique = unique;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isId() {
        return id;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public Integer getLength() {
        return length;
    }
}
