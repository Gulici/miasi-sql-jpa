package pwr.miasi.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import pwr.miasi.entity.EntityDefinition;
import pwr.miasi.entity.EntityModel;
import pwr.miasi.entity.RelationField;
import pwr.miasi.entity.ScalarField;

public class EntityGenerator {
    private final STGroup templates;

    /** Loads StringTemplate group used to render entity classes. */
    public EntityGenerator() {
        var resource = EntityGenerator.class.getResource("/templates/entity.stg");
        if (resource == null) {
            throw new IllegalStateException("Missing template resource: /templates/entity.stg");
        }
        this.templates = new STGroupFile(resource, "UTF-8", '<', '>');
    }

    /** Generates one Java file per entity definition. */
    public void generate(EntityModel entityModel, Path outputDir, String basePackage) throws IOException {
        Files.createDirectories(outputDir);
        for (EntityDefinition entity : entityModel.getEntities()) {
            String rendered = renderEntity(entity, basePackage);
            Path filePath = outputDir.resolve(entity.getClassName() + ".java");
            Files.writeString(filePath, rendered);
        }
    }

    private String renderEntity(EntityDefinition entity, String basePackage) {
        // Split id field from regular scalar fields for cleaner template rendering.
        ST st = templates.getInstanceOf("entity");
        ScalarField idField = entity.getScalarFields().stream()
                .filter(ScalarField::isId)
                .findFirst()
                .orElse(null);
        List<ScalarField> regularFields = entity.getScalarFields().stream()
                .filter(field -> !field.isId())
                .sorted(Comparator.comparing(ScalarField::getName))
                .toList();

        st.add("packageName", basePackage);
        st.add("className", entity.getClassName());
        st.add("tableName", entity.getTableName());
        st.add("imports", buildImports(entity));
        st.add("idField", idField);
        st.add("scalarFields", regularFields);
        st.add("relationFields", entity.getRelationFields());
        return st.render();
    }

    private List<String> buildImports(EntityDefinition entity) {
        // Build only imports needed by field types and relation collections.
        Set<String> imports = new TreeSet<>();
        for (ScalarField field : entity.getScalarFields()) {
            if ("LocalDateTime".equals(field.getType())) {
                imports.add("java.time.LocalDateTime");
            }
        }
        for (RelationField relationField : entity.getRelationFields()) {
            if (relationField.isOneToMany()) {
                imports.add("java.util.ArrayList");
                imports.add("java.util.List");
            }
            if (relationField.isManyToMany()) {
                imports.add("java.util.LinkedHashSet");
                imports.add("java.util.Set");
            }
        }
        return new ArrayList<>(imports);
    }
}
