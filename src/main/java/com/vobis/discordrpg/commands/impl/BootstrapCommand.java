package com.vobis.discordrpg.commands.impl;

import com.vobis.discordrpg.DiscordRPG;
import com.vobis.discordrpg.commands.handler.ICommand;
import com.vobis.discordrpg.lang.Translations;
import com.vobis.discordrpg.util.MessageUtils;
import com.vobis.discordrpg.zones.ZoneManager;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.PermissionSet;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BootstrapCommand implements ICommand {

    @Override
    public Mono<?> execute(List<String> args, Message message, Member member, Guild guild, TextChannel textChannel) {
        if (message.getContent().get().contains(guild.getName())) {
            return bootstrapGuild(guild);
        }

        return MessageUtils.createAutoDeleteMessage(Translations.getFor("bootstrap.warning"), textChannel, 15000);
    }

    private Mono<Void> bootstrapGuild(Guild guild) {
        return clearAllChannels(guild)
                .then(createDefaultChannels(guild))
                .then(Mono.when(
                        setupDefaultPermissions(guild),
                        setupMembers(guild)
                ));
    }

    private Mono<Void> createDefaultChannels(Guild guild) {
        return guild.createCategory(spec -> spec.setName("lobby"))
                .zipWith(guild.getEveryoneRole())
                .flatMap(TupleUtils.function((category, everyoneRole) ->
                        guild.createTextChannel(spec -> {
                            spec.setName("lobby");
                            spec.setParentId(category.getId());
                            spec.setPermissionOverwrites(Stream.of(PermissionOverwrite.forRole(everyoneRole.getId(), ZoneManager.DEFAULT_PERMISSIONS, PermissionSet.none())).collect(Collectors.toSet()));
                        }).then(guild.createTextChannel(spec -> {
                            spec.setName("rules");
                            spec.setParentId(category.getId());
                            spec.setPermissionOverwrites(Stream.of(PermissionOverwrite.forRole(everyoneRole.getId(), ZoneManager.READ_ONLY, PermissionSet.none())).collect(Collectors.toSet()));
                        }).flatMap(textChannel -> textChannel.createMessage(Translations.templateFor("bootstrap.rules", guild.getName()))))
                ))
                .then(guild.createCategory(spec -> spec.setName("game")))
                .then();
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
                .concatMap(member -> instance.getZoneManager().movePlayerToZone(member, DiscordRPG.INSTANCE.getZoneMap().getZone("rithington")).thenReturn(member))
                .doOnNext(member -> instance.getPlayers().createPlayer(member))
                .then();
    }

    private Mono<?> setupDefaultPermissions(Guild guild) {
        return guild.getEveryoneRole()
                .flatMap(role -> role.edit(spec -> spec.setPermissions(PermissionSet.none())));
    }
}
