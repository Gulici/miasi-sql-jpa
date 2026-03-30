package pwr.miasi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemaModel {
    private final List<Table> tables = new ArrayList<>();

    /** Adds parsed table definition to schema container. */
    public void addTable(Table table) {
        tables.add(table);
    }

    /** Read-only access to all parsed tables. */
    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    /** Finds table by SQL name (case-insensitive). */
    public Table getTableByName(String tableName) {
        return tables.stream()
                .filter(t -> t.getName().equalsIgnoreCase(tableName))
                .findFirst()
                .orElse(null);
    }
}
