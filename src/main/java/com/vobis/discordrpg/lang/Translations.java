package com.vobis.discordrpg.lang;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

public class Translations {

    private static final String FILE_PATH = "./lang.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, Map<String, String>> translations;

    static {
        try {
            translations = objectMapper.readValue(ClassLoader.getSystemResourceAsStream(FILE_PATH), new TypeReference<Map<String, Map<String, String>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load language file", e);
        }
    }

    public static String getFor(String key) {
        String result = translations.get("en-gb").get(key);

        if(result != null) {
            return result;
        }

        return key;
    }

    public static String templateFor(String key, Object...objects) {
        String result = translations.get("en-gb").get(key);

        if(result != null) {
            return MessageFormat.format(result, objects);
        }

        return key;
    }
}
