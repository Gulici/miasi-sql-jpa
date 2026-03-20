package pwr.miasi;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import pwr.miasi.antlr4.SqlLexer;
import pwr.miasi.antlr4.SqlParser;
import pwr.miasi.model.Table;
import pwr.miasi.parser.SchemaVisitor;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String sql = """
        CREATE TABLE users (
            id BIGINT PRIMARY KEY,
            username VARCHAR(100) NOT NULL,
            email VARCHAR(200) UNIQUE
        );
        """;

        SqlLexer lexer = new SqlLexer(CharStreams.fromString(sql));
        SqlParser parser = new SqlParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.schema();

        SchemaVisitor visitor = new SchemaVisitor();
        visitor.visit(tree);

        List<Table> tables = visitor.getTables();
        for (Table t : tables) {
            System.out.println(t);
        }
    }
}