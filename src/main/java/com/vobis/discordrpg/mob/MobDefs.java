package com.vobis.discordrpg.mob;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class MobDefs {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MOBS_FILE = "game/mobs.json";

    private Map<String, MobDef> mobs;

    public MobDefs() {
        this.mobs = loadMobs();
    }

    private Map<String, MobDef> loadMobs() {
        try {
            Map<String, MobDef> mobs = objectMapper.readValue(ClassLoader.getSystemResourceAsStream(MOBS_FILE), new TypeReference<Map<String, MobDef>>() {});
            mobs.forEach((key, value) -> value.setKeyName(key));
            return mobs;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load mobs", e);
        }
    }

    public MobDef getMob(String name) {
        return mobs.get(name);
    }
}
