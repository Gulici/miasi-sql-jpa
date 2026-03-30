package pwr.miasi.parser;

public final class SqlTypeMapper {
    private SqlTypeMapper() {
    }

    /** Maps supported SQL scalar types to Java field types. */
    public static String toJavaType(String sqlType) {
        String normalized = sqlType.toUpperCase();
        if (normalized.startsWith("VARCHAR")) {
            return "String";
        }
        return switch (normalized) {
            case "INT" -> "Integer";
            case "BIGINT" -> "Long";
            case "TEXT" -> "String";
            case "TIMESTAMP" -> "LocalDateTime";
            default -> "String";
        };
    }
}
