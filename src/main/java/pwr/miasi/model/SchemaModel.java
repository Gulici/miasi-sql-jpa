package pwr.miasi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemaModel {
    private final List<Table> tables = new ArrayList<>();

    public void addTable(Table table) {
        tables.add(table);
    }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public Table getTableByName(String tableName) {
        return tables.stream()
                .filter(t -> t.getName().equalsIgnoreCase(tableName))
                .findFirst()
                .orElse(null);
    }
}
