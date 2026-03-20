package pwr.miasi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Column {
    public String name;
    public String type;
    public boolean isId;
    private boolean nullable = true;
    private boolean unique = false;

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isId=" + isId +
                ", nullable=" + nullable +
                ", unique=" + unique +
                '}';
    }
}
