package com.vobis.discordrpg.util;

import com.vobis.discordrpg.lang.Translations;
import com.vobis.discordrpg.player.Player;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Mono;

public class SkillUtils {

    public static Mono<?> addPlayerXP(Player player, String stat, int xp, TextChannel textChannel) {
        if (player.getPlayerStat(stat).addXP(xp)) {
            return MessageUtils
                    .createAutoDeleteMessage(Translations
                            .templateFor("skill.levelUp", player.getName(), stat, player.getPlayerStat(stat).getLevel()), textChannel, 10000);
        } else {
            return Mono.empty();
        }
    }
}
