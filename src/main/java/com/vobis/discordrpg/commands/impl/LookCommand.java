package com.vobis.discordrpg.commands.impl;

import com.vobis.discordrpg.DiscordRPG;
import com.vobis.discordrpg.commands.handler.ICommand;
import com.vobis.discordrpg.lang.Translations;
import com.vobis.discordrpg.mob.MobDef;
import com.vobis.discordrpg.util.MessageUtils;
import com.vobis.discordrpg.zones.Zone;
import com.vobis.discordrpg.zones.ZoneMap;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class LookCommand implements ICommand {

    @Override
    public Mono<?> execute(List<String> args, Message message, Member member, Guild guild, TextChannel textChannel) {
        return look(member, textChannel)
                .flatMap(msg -> message.delete().thenReturn(msg));
    }

    private Mono<Void> look(Member member, TextChannel channel) {
        Zone zone = DiscordRPG.INSTANCE.getZoneMap().getZone(channel.getName());
        Zone[] neighbours = DiscordRPG.INSTANCE.getZoneMap().getNeighbours(zone.getLocation());

        String message = Translations.templateFor("look.message",
                member.getMention(),
                zone.getName(),
                nearbyMobs(zone),
                getNeighbourString(neighbours));

        return MessageUtils.createAutoDeleteMessage(message, channel, 10000);
    }

    private String nearbyMobs(Zone zone) {
        return zone.getMobs().stream()
                .map(MobDef::getKeyName)
                .collect(Collectors.joining(", "));
    }

    private String getNeighbourString(Zone[] neighbours) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < ZoneMap.DIRECTIONS.size(); i++) {
            result.append(Translations.templateFor("look.direction", ZoneMap.DIRECTIONS.get(i), neighbours[i] != null ? neighbours[i].getName() : Translations.getFor("look.nothing")));
        }

        return result.toString();
    }
}
