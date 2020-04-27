package com.vobis.discordrpg.commands.impl;

import com.vobis.discordrpg.DiscordRPG;
import com.vobis.discordrpg.commands.handler.ICommand;
import com.vobis.discordrpg.zones.ZoneMap;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Mono;

import java.util.List;

public class JoinCommand implements ICommand {

    @Override
    public Mono<?> execute(List<String> args, Message message, Member member, Guild guild, TextChannel textChannel) {
        DiscordRPG instance = DiscordRPG.INSTANCE;
        instance.getPlayers().createPlayer(member);
        return instance.getZoneManager().movePlayerToZone(member, instance.getZoneMap().getZone("rithington"));
    }
}
