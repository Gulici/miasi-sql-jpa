package pwr.miasi;

import java.nio.file.Files;
import java.nio.file.Path;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;

import pwr.miasi.analyzer.RelationshipAnalyzer;
import pwr.miasi.antlr4.SqlLexer;
import pwr.miasi.antlr4.SqlParser;
import pwr.miasi.entity.EntityModel;
import pwr.miasi.generator.EntityGenerator;
import pwr.miasi.model.SchemaModel;
import pwr.miasi.parser.SchemaVisitor;

public class Main {
    /**
      CLI entry point. Expected args: input.sql outputDir [--package=...] [--dry-run]
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            printUsage();
            return;
        }

        boolean dryRun = false;
        String basePackage = "generated.entities";
        String inputPath = null;
        String outputPath = null;

        for (String arg : args) {
            if ("--dry-run".equals(arg)) {
                dryRun = true;
                continue;
            }
            if (arg.startsWith("--package=")) {
                basePackage = arg.substring("--package=".length());
                continue;
            }
            if (inputPath == null) {
                inputPath = arg;
            } else if (outputPath == null) {
                outputPath = arg;
            } else {
                basePackage = arg;
            }
        }
        if (inputPath == null || outputPath == null) {
            printUsage();
            return;
        }

        // 1) Parse SQL input into an in-memory schema model.
        String sql = Files.readString(Path.of(inputPath));
        SqlLexer lexer = new SqlLexer(CharStreams.fromString(sql));
        SqlParser parser = new SqlParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new ThrowingErrorListener());
        ParseTree tree = parser.schema();

        SchemaVisitor visitor = new SchemaVisitor();
        visitor.visit(tree);
        SchemaModel schemaModel = visitor.getSchemaModel();

        // 2) Convert schema model into entity model with JPA relations.
        RelationshipAnalyzer analyzer = new RelationshipAnalyzer();
        EntityModel entityModel = analyzer.analyze(schemaModel);

        if (dryRun) {
            System.out.println("Parsed tables: " + schemaModel.getTables().size());
            System.out.println("Generated entities: " + entityModel.getEntities().size());
            entityModel.getEntities().forEach(entity ->
                    System.out.println("- " + entity.getClassName() + " (table: " + entity.getTableName() + ")"));
            return;
        }

        // 3) Generate Java files from templates.
        EntityGenerator generator = new EntityGenerator();
        Path outputDirectory = Path.of(outputPath);
        generator.generate(entityModel, outputDirectory, basePackage);
        System.out.println("Entities generated in: " + outputDirectory.toAbsolutePath());
    }

    /* Prints CLI usage for invalid/missing arguments. */
    private static void printUsage() {
        System.out.println("Usage: java -jar app.jar <input.sql> <outputDir> [--package=your.package] [--dry-run]");
    }

    /* Converts parser syntax errors into clear runtime exceptions. */
    private static final class ThrowingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e
        ) {
            throw new IllegalArgumentException("SQL syntax error at " + line + ":" + charPositionInLine + " - " + msg, e);
        }
    }
}