package com.vobis.discordrpg.zones;

import com.vobis.discordrpg.mob.Mob;
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
    private List<Mob> mobs;
    private Location location;

    private final List<TextChannel> channels = Collections.synchronizedList(new ArrayList<>());
}
