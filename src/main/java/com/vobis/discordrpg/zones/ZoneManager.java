package com.vobis.discordrpg.zones;

import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import reactor.core.publisher.Mono;

public class ZoneManager {

    public static final int MAX_PLAYERS_IN_CHANNEL = 15;

    private static final PermissionSet DEFAULT_PERMISSIONS = PermissionSet.of(
            Permission.READ_MESSAGE_HISTORY
    );

    public Mono<Void> movePlayerToZone(Member member, Zone zone) {
        return member.getGuild()
                .flatMap(guild -> findOrCreateChannel(guild, zone))
                .flatMap(channel -> channel.addMemberOverwrite(member.getId(), PermissionOverwrite.forMember(member.getId(), DEFAULT_PERMISSIONS, PermissionSet.none())));
    }

    private Mono<TextChannel> findOrCreateChannel(Guild guild, Zone zone) {
        return Mono.just(zone).flatMapIterable(Zone::getChannels).next()
                .switchIfEmpty(createChannel(guild, zone));
    }

    private boolean memberCanJoin(PermissionSet permissions, TextChannel channel) {
        return !permissions.contains(Permission.READ_MESSAGE_HISTORY);
    }

    private Mono<TextChannel> createChannel(Guild guild, Zone zone) {
        return guild.createTextChannel(spec -> spec.setName(zone.getName()))
                .doOnNext(zone.getChannels()::add);
    }
}
