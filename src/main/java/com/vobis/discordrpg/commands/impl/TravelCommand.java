package com.vobis.discordrpg.commands.impl;

import com.vobis.discordrpg.DiscordRPG;
import com.vobis.discordrpg.commands.handler.ICommand;
import com.vobis.discordrpg.lang.Translations;
import com.vobis.discordrpg.util.MessageUtils;
import com.vobis.discordrpg.zones.Zone;
import com.vobis.discordrpg.zones.ZoneMap;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Mono;

import java.util.List;

public class TravelCommand implements ICommand {

    @Override
    public Mono<?> execute(List<String> args, Message message, Member member, Guild guild, TextChannel textChannel) {
        Zone currentZone = DiscordRPG.INSTANCE.getZoneMap().getZone(textChannel.getName());
        Zone[] neighbours = DiscordRPG.INSTANCE.getZoneMap().getNeighbours(currentZone.getLocation());

        if (args.isEmpty()) {
            return MessageUtils.createAutoDeleteMessage(Translations.templateFor("travel.where", member.getMention()), textChannel, 5000);
        }

        String directionString = args.get(0).toLowerCase();

        if (!ZoneMap.DIRECTIONS.contains(directionString)) {
            return MessageUtils.createAutoDeleteMessage(Translations.templateFor("travel.invalidDirection", member.getMention()), textChannel, 5000);
        }

        int direction = ZoneMap.DIRECTIONS.indexOf(directionString);
        Zone destination = neighbours[direction];

        if (destination == null) {
            return textChannel.createMessage(Translations.templateFor("travel.nothing", member.getMention()));
        } else {
            return DiscordRPG.INSTANCE.getZoneManager().movePlayerToZone(member, destination)
                    .flatMap(channel -> textChannel
                            .createMessage(Translations.templateFor("travel.moved", member.getMention(), channel.getMention()))
                            .thenReturn(channel))
                    .flatMap(channel -> DiscordRPG.INSTANCE.getZoneManager()
                            .resetChannelPerms(member, guild, channel));
        }
    }
}
