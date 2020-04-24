package com.vobis.discordrpg.commands;

import com.vobis.discordrpg.DiscordRPG;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.PermissionSet;
import reactor.core.publisher.Mono;

public class BootstrapCommand implements ICommand {

    @Override
    public Mono<Void> execute(Message message) {
        return message.getGuild()
                .flatMap(guild -> {
                    if (message.getContent().get().contains(guild.getName())) {
                        return bootstrapGuild(guild);
                    }

                    return message.getChannel().flatMap(channel -> channel.createMessage("**WARNING** this command will completely wipe all channels and roles from this server. If you're sure you want to continue, run this command again with your server name as a parameter."));
                })
                .then();
    }

    private Mono<Void> bootstrapGuild(Guild guild) {
        return clearAllChannels(guild)
                .then(Mono.when(
                        createLobby(guild),
                        setupDefaultPermissions(guild),
                        setupMembers(guild)
                ));
    }

    private Mono<Void> clearAllChannels(Guild guild) {
        return guild.getChannels().flatMap(Channel::delete).then();
    }

    private Mono<?> createLobby(Guild guild) {
        return guild.createTextChannel(spec -> {
            spec.setName("lobby");
        }).flatMap(channel -> channel.createMessage("Welcome to the lobby."));
    }

    private Mono<?> setupMembers(Guild guild) {
        DiscordRPG instance = DiscordRPG.INSTANCE;

        return guild.getMembers()
                .concatMap(member -> instance.getZoneManager().movePlayerToZone(member, DiscordRPG.INSTANCE.getZoneMap().getZone("rithington")))
                .then();
    }

    private Mono<?> setupDefaultPermissions(Guild guild) {
        return guild.getEveryoneRole()
                .flatMap(role -> role.edit(spec -> spec.setPermissions(PermissionSet.none())));
    }
}
