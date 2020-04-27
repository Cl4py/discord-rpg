package com.vobis.discordrpg.commands.handler;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ICommand {
    Mono<?> execute(List<String> args, Message message, Member member, Guild guild, TextChannel textChannel);
}
