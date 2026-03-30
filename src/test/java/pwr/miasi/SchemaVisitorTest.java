package pwr.miasi;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import pwr.miasi.antlr4.SqlLexer;
import pwr.miasi.antlr4.SqlParser;
import pwr.miasi.model.SchemaModel;
import pwr.miasi.model.Table;
import pwr.miasi.parser.SchemaVisitor;

// czy parser + visitor dobrze robi model (PK/FK)
class SchemaVisitorTest {
    @Test
    void shouldBuildSchemaModelWithPkAndFk() {
        String sql = """
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY,
                    username VARCHAR(100) NOT NULL UNIQUE
                );
                CREATE TABLE posts (
                    id BIGINT PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

        SchemaModel model = parse(sql);
        assertEquals(2, model.getTables().size());

        Table posts = model.getTableByName("posts");
        assertNotNull(posts);
        assertTrue(posts.getPrimaryKey().isPresent());
        assertEquals(1, posts.getPrimaryKey().get().getColumnNames().size());
        assertEquals(1, posts.getForeignKeys().size());
        assertEquals("users", posts.getForeignKeys().get(0).getReferencedTable());
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
