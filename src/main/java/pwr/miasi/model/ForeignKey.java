package pwr.miasi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForeignKey {
    private final String name;
    private final List<String> localColumnNames;
    private final String referencedTable;
    private final List<String> referencedColumnNames;

    public ForeignKey(String name, List<String> localColumnNames, String referencedTable, List<String> referencedColumnNames) {
        this.name = name;
        this.localColumnNames = new ArrayList<>(localColumnNames);
        this.referencedTable = referencedTable;
        this.referencedColumnNames = new ArrayList<>(referencedColumnNames);
    }

    public String getName() {
        return name;
    }

    public List<String> getLocalColumnNames() {
        return Collections.unmodifiableList(localColumnNames);
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public List<String> getReferencedColumnNames() {
        return Collections.unmodifiableList(referencedColumnNames);
    }

    @Override
    public String toString() {
        return "ForeignKey{" +
                "name='" + name + '\'' +
                ", localColumnNames=" + localColumnNames +
                ", referencedTable='" + referencedTable + '\'' +
                ", referencedColumnNames=" + referencedColumnNames +
                '}';
    }
}
