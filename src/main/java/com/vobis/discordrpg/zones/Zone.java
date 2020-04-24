package com.vobis.discordrpg.zones;

import com.vobis.discordrpg.mob.Mob;
import discord4j.core.object.entity.TextChannel;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Zone {
    private final String name;
    private final String description;
    private final List<Mob> mobs;

    private final List<TextChannel> channels = Collections.synchronizedList(new ArrayList<>());
}
