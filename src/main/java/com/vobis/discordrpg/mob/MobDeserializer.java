package com.vobis.discordrpg.mob;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.vobis.discordrpg.DiscordRPG;

import java.io.IOException;

public class MobDeserializer extends JsonDeserializer<MobDef> {

    @Override
    public MobDef deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String mobName = p.getText();
        return DiscordRPG.INSTANCE.getMobs().getMob(mobName);
    }
}
