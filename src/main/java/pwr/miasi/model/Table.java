package pwr.miasi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Table {
    private final String name;
    private final List<Column> columns = new ArrayList<>();
    private final List<ForeignKey> foreignKeys = new ArrayList<>();
    private PrimaryKey primaryKey;

    public Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public Optional<PrimaryKey> getPrimaryKey() {
        return Optional.ofNullable(primaryKey);
    }

    public List<ForeignKey> getForeignKeys() {
        return Collections.unmodifiableList(foreignKeys);
    }

    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public void addForeignKey(ForeignKey foreignKey) {
        foreignKeys.add(foreignKey);
    }

    public Column getColumnByName(String columnName) {
        return columns.stream()
                .filter(column -> column.getName().equalsIgnoreCase(columnName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                ", primaryKey=" + primaryKey +
                ", foreignKeys=" + foreignKeys +
                '}';
    }
}
