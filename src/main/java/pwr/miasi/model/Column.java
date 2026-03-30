package pwr.miasi.model;

public class Column {
    private final String name;
    private final String sqlType;
    private final String javaType;
    private Integer length;
    private boolean primaryKeyPart;
    private boolean nullable = true;
    private boolean unique;

    public Column(String name, String sqlType, String javaType) {
        this.name = name;
        this.sqlType = sqlType;
        this.javaType = javaType;
    }

    public String getName() {
        return name;
    }

    public String getSqlType() {
        return sqlType;
    }

    public String getJavaType() {
        return javaType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean isPrimaryKeyPart() {
        return primaryKeyPart;
    }

    public void setPrimaryKeyPart(boolean primaryKeyPart) {
        this.primaryKeyPart = primaryKeyPart;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", sqlType='" + sqlType + '\'' +
                ", javaType='" + javaType + '\'' +
                ", length=" + length +
                ", primaryKeyPart=" + primaryKeyPart +
                ", nullable=" + nullable +
                ", unique=" + unique +
                '}';
    }
}
