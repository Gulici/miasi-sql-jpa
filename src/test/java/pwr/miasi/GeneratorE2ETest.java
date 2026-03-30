package pwr.miasi;

import java.nio.file.Files;
import java.nio.file.Path;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pwr.miasi.analyzer.RelationshipAnalyzer;
import pwr.miasi.antlr4.SqlLexer;
import pwr.miasi.antlr4.SqlParser;
import pwr.miasi.entity.EntityModel;
import pwr.miasi.generator.EntityGenerator;
import pwr.miasi.model.SchemaModel;
import pwr.miasi.parser.SchemaVisitor;

//Test end-to-end: od SQL do wygenerowanych plików encji
class GeneratorE2ETest {
    @TempDir
    Path tempDir;

    @Test
    void shouldGenerateEntityFilesFromSchema() throws Exception {
        String sql = """
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY,
                    username VARCHAR(100) NOT NULL UNIQUE
                );
                CREATE TABLE posts (
                    id BIGINT PRIMARY KEY,
                    user_id BIGINT NOT NULL REFERENCES users(id),
                    body TEXT
                );
                """;

        SchemaModel schemaModel = parse(sql);
        EntityModel entityModel = new RelationshipAnalyzer().analyze(schemaModel);
        new EntityGenerator().generate(entityModel, tempDir, "demo.entities");

        Path userEntity = tempDir.resolve("User.java");
        Path postEntity = tempDir.resolve("Post.java");
        assertTrue(Files.exists(userEntity));
        assertTrue(Files.exists(postEntity));
        assertTrue(Files.readString(postEntity).contains("@ManyToOne"));
        assertTrue(Files.readString(userEntity).contains("@OneToMany"));
    }

    private SchemaModel parse(String sql) {
        SqlLexer lexer = new SqlLexer(CharStreams.fromString(sql));
        SqlParser parser = new SqlParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.schema();
        SchemaVisitor visitor = new SchemaVisitor();
        visitor.visit(tree);
        return visitor.getSchemaModel();
    }
}
