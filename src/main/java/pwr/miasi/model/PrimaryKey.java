package pwr.miasi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimaryKey {
    private final List<String> columnNames;

    public PrimaryKey(List<String> columnNames) {
        this.columnNames = new ArrayList<>(columnNames);
    }

    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }

    @Override
    public String toString() {
        return "PrimaryKey{" +
                "columnNames=" + columnNames +
                '}';
    }
}
