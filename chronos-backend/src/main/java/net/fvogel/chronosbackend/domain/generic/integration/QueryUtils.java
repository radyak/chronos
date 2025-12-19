package net.fvogel.chronosbackend.domain.generic.integration;

import java.util.Map;
import java.util.stream.Collectors;

public class QueryUtils {

    public static final String CURLY_BRACES = "{}";
    public static final String ROUND_BRACES = "()";
    public static final String SQUARE_BRACES = "[]";

    public static String wrapWith(String content, String wrapper) {
        if (wrapper == null) {
            return content;
        } else if (wrapper.equals(CURLY_BRACES)) {
            return "{" + content + "}";
        } else if (wrapper.equals(ROUND_BRACES)) {
            return "(" + content + ")";
        } else if (wrapper.equals(SQUARE_BRACES)) {
            return "[" + content + "]";
        }
        return wrapper + content + wrapper;
    }

    public static String formatMapToPropertiesObject(Map<String, String> properties) {
        String listedProps = properties.keySet().stream().map(key -> {
            String value = properties.get(key);
            return key + ":" + (value == null ? null : wrapWith(value, "'"));
        }).collect(Collectors.joining(", "));
        return wrapWith(listedProps, CURLY_BRACES);
    }

    public static String formatMapToPropertiesUpdate(Map<String, String> properties, String elementAlias) {
        return properties.keySet().stream()
                .filter(key -> !"id".equals(key))
                .map(key -> {
                    String value = properties.get(key);
                    return elementAlias + "." + key + "=" + wrapWith(value, "'");
                })
                .collect(Collectors.joining(", "));
    }

}
