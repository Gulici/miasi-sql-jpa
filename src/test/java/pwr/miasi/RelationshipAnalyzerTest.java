package pwr.miasi;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import pwr.miasi.analyzer.RelationshipAnalyzer;
import pwr.miasi.antlr4.SqlLexer;
import pwr.miasi.antlr4.SqlParser;
import pwr.miasi.entity.EntityDefinition;
import pwr.miasi.entity.EntityModel;
import pwr.miasi.model.SchemaModel;
import pwr.miasi.parser.SchemaVisitor;


//czy relacje są dobrze wykrywane (1:N, N:M)
class RelationshipAnalyzerTest {
    @Test
    void shouldDetectOneToManyAndManyToMany() {
        String sql = """
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY
                );
                CREATE TABLE posts (
                    id BIGINT PRIMARY KEY,
                    user_id BIGINT NOT NULL REFERENCES users(id)
                );
                CREATE TABLE courses (
                    id BIGINT PRIMARY KEY
                );
                CREATE TABLE user_course (
                    user_id BIGINT NOT NULL,
                    course_id BIGINT NOT NULL,
                    CONSTRAINT pk_user_course PRIMARY KEY (user_id, course_id),
                    CONSTRAINT fk_uc_user FOREIGN KEY (user_id) REFERENCES users(id),
                    CONSTRAINT fk_uc_course FOREIGN KEY (course_id) REFERENCES courses(id)
                );
                """;

        SchemaModel schemaModel = parse(sql);
        EntityModel entityModel = new RelationshipAnalyzer().analyze(schemaModel);
        assertEquals(3, entityModel.getEntities().size());

        EntityDefinition users = entityModel.getByTableName("users");
        assertNotNull(users);
        assertTrue(users.getRelationFields().stream().anyMatch(r -> r.isOneToMany() && r.getTargetEntity().equals("Post")));
        assertTrue(users.getRelationFields().stream().anyMatch(r -> r.isManyToMany() && r.getTargetEntity().equals("Course")));
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
