package com.vobis.discordrpg.player;

import com.vobis.discordrpg.DiscordRPG;
import com.vobis.discordrpg.lang.Translations;
import com.vobis.discordrpg.util.MessageUtils;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class Players {

    private final Map<Snowflake, Player> players = new HashMap<>();

    public Player savePlayer(Snowflake snowflake, Player player) {
        players.put(snowflake, player);
        return player;
    }

    public Player createPlayer(Member member) {
        return savePlayer(member.getId(), new Player(member.getDisplayName()));
    }

    public Player getPlayer(Member member) {
        return players.computeIfAbsent(member.getId(), $ -> new Player(member.getDisplayName()));
    }

    public Mono<Void> die(TextChannel textChannel, Member member) {
        return MessageUtils.createAutoDeleteMessage(Translations.templateFor("player.killed", member.getDisplayName()), textChannel, 10000)
                .and(DiscordRPG.INSTANCE.getZoneManager().movePlayerToZone(member, DiscordRPG.INSTANCE.getZoneMap().getZone("rithington")));
    }
}
