package pwr.miasi.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// lista encji do wygenerowania 
public class EntityModel {
    private final List<EntityDefinition> entities = new ArrayList<>();

    /** Adds generated entity definition to model. */
    public void addEntity(EntityDefinition entity) {
        entities.add(entity);
    }

    /** Read-only access to all entity definitions. */
    public List<EntityDefinition> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    /** Finds entity by original table name (case-insensitive). */
    public EntityDefinition getByTableName(String tableName) {
        return entities.stream()
                .filter(entity -> entity.getTableName().equalsIgnoreCase(tableName))
                .findFirst()
                .orElse(null);
    }
}
