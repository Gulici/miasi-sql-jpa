package pwr.miasi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Table {
    public String name;
    public List<Column> columns = new ArrayList<>();

    public Table(String name) { this.name = name; }

    public void addColumn(Column column) { columns.add(column); }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                '}';
    }
}
