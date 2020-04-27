package com.vobis.discordrpg.zones;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vobis.discordrpg.mob.MobDef;
import com.vobis.discordrpg.mob.MobDeserializer;
import discord4j.core.object.entity.TextChannel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class Zone {
    private String name;
    private String channelName;
    private String description;
    private Location location;

    @JsonDeserialize(contentUsing = MobDeserializer.class)
    private List<MobDef> mobs;

    private final List<TextChannel> channels = Collections.synchronizedList(new ArrayList<>());
}
