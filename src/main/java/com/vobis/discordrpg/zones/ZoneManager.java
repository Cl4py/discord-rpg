package com.vobis.discordrpg.zones;

import discord4j.core.object.ExtendedPermissionOverwrite;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Category;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class ZoneManager {

    public static final int MAX_PLAYERS_IN_CHANNEL = 15;

    public static final PermissionSet DEFAULT_PERMISSIONS = PermissionSet.of(
            Permission.VIEW_CHANNEL,
            Permission.SEND_MESSAGES,
            Permission.READ_MESSAGE_HISTORY
    );

    public static final PermissionSet READ_ONLY = PermissionSet.of(
            Permission.VIEW_CHANNEL,
            Permission.READ_MESSAGE_HISTORY
    );

    public Mono<TextChannel> movePlayerToZone(Member member, Zone zone) {
        return member.getGuild()
                .flatMap(guild -> findOrCreateChannel(guild, zone))
                .flatMap(channel -> channel.addMemberOverwrite(member.getId(), PermissionOverwrite.forMember(member.getId(), DEFAULT_PERMISSIONS, PermissionSet.none())).thenReturn(channel));
    }

    public Mono<Void> resetChannelPerms(Member member, Guild guild, TextChannel... exclude) {
        List<TextChannel> excludes = Arrays.asList(exclude);

        return guild.getChannels()
                .ofType(TextChannel.class)
                .filter(channel -> !excludes.contains(channel))
                .flatMap(channel -> channel.getOverwriteForMember(member.getId()).map(ExtendedPermissionOverwrite::delete).orElse(Mono.empty()))
                .then();
    }

    private Mono<TextChannel> findOrCreateChannel(Guild guild, Zone zone) {
        return guild.getChannels()
                .filter(guildChannel -> guildChannel.getName().equals(zone.getChannelName()))
                .ofType(TextChannel.class)
                .next()
                .switchIfEmpty(createChannel(guild, zone));
    }

    private Mono<TextChannel> createChannel(Guild guild, Zone zone) {
        return getGameCategory(guild)
                .flatMap(category -> guild.createTextChannel(spec -> {
                    spec.setName(zone.getChannelName());
                    spec.setParentId(category.getId());
                }).flatMap(channel -> channel.createMessage("Welcome to " + zone.getName() + ". " + zone.getDescription())
                        .thenReturn(channel)));
    }

    private Mono<Category> getGameCategory(Guild guild) {
        return guild.getChannels()
                .ofType(Category.class)
                .filter(category -> category.getName().equals("game"))
                .next();
    }
}
