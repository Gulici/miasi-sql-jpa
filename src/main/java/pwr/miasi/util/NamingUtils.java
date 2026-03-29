package pwr.miasi.util;

public final class NamingUtils {
    private NamingUtils() {
    }

    public static String toClassName(String name) {
        StringBuilder sb = new StringBuilder();
        for (String token : name.split("_")) {
            if (token.isBlank()) {
                continue;
            }
            String normalized = singularize(token.toLowerCase());
            sb.append(Character.toUpperCase(normalized.charAt(0)));
            if (normalized.length() > 1) {
                sb.append(normalized.substring(1));
            }
        }
        return sb.toString();
    }

    public static String toFieldName(String name) {
        String className = toClassName(name);
        if (className.isEmpty()) {
            return name;
        }
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    public static String pluralize(String base) {
        if (base.endsWith("s")) {
            return base + "es";
        }
        return base + "s";
    }

    private static String singularize(String token) {
        if (token.endsWith("ies") && token.length() > 3) {
            return token.substring(0, token.length() - 3) + "y";
        }
        if (token.endsWith("s") && token.length() > 1) {
            return token.substring(0, token.length() - 1);
        }
        return token;
    }
}
