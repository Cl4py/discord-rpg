package com.vobis.discordrpg.commands;

import com.vobis.discordrpg.DiscordRPG;
import com.vobis.discordrpg.zones.ZoneMap;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.PermissionSet;
import reactor.core.publisher.Mono;

public class BootstrapCommand implements ICommand {

    @Override
    public Mono<Void> execute(Message message) {
        return message.getChannel()
                .flatMap(channel -> channel.createMessage("Bootstrapping..."))
                .then(message.getGuild().flatMap(this::bootstrapGuild));
    }

    private Mono<Void> clearAllChannels(Guild guild) {
        return guild.getChannels().flatMap(Channel::delete).then();
    }

    private Mono<Void> bootstrapGuild(Guild guild) {
        return Mono.when(
                clearAllChannels(guild),
                createLobby(guild),
                setupDefaultPermissions(guild),
                setupMembers(guild)
        );
    }

    private Mono<?> createLobby(Guild guild) {
        return guild.createTextChannel(spec -> {
            spec.setName("lobby");
        }).flatMap(channel -> channel.createMessage("Welcome to the lobby."));
    }

    private Mono<?> setupMembers(Guild guild) {
        DiscordRPG instance = DiscordRPG.INSTANCE;

        return guild.getMembers()
                .flatMap(member -> instance.getZoneManager().movePlayerToZone(member, ZoneMap.DOUJAB))
                .then();
    }

    private Mono<?> setupDefaultPermissions(Guild guild) {
        return guild.getEveryoneRole()
                .flatMap(role -> role.edit(spec -> spec.setPermissions(PermissionSet.none())));
    }
}
