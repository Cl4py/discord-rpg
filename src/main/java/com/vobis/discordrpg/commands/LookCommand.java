package com.vobis.discordrpg.commands;

import com.vobis.discordrpg.DiscordRPG;
import com.vobis.discordrpg.zones.Zone;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.time.Duration;
import java.util.stream.Stream;

public class LookCommand implements ICommand {

    private static final String[] DIRECTIONS = {"north", "east", "south", "west"};
    private static final Duration MESSAGE_TIME = Duration.ofSeconds(5);

    @Override
    public Mono<?> execute(Message message) {
        return Mono.zip(message.getAuthorAsMember(), message.getGuild(), message.getChannel().ofType(TextChannel.class))
                .flatMap(TupleUtils.function(this::look))
                .flatMap(msg -> message.delete().thenReturn(msg))
                .delayElement(MESSAGE_TIME)
                .flatMap(Message::delete);
    }

    private Mono<Message> look(Member member, Guild guild, TextChannel channel) {
        String channelName = channel.getName();
        Zone zone = DiscordRPG.INSTANCE.getZoneMap().getZone(channelName);
        Zone[] neighbours = DiscordRPG.INSTANCE.getZoneMap().getNeighbours(zone.getLocation());

        StringBuilder builder = new StringBuilder();
        builder.append("<@").append(member.getId().asString()).append(">").append(", you are currently in ").append(zone.getName()).append(". ").append("You look around and see: nothing").append("\n");

        for(int i = 0; i < DIRECTIONS.length; i++) {
            builder.append("To your ").append(DIRECTIONS[i]).append(": ").append(neighbours[i] != null ? neighbours[i].getName() : "nothing").append(".\n");
        }

        return channel.createMessage(builder.toString());
    }
}
