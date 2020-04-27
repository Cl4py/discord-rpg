package com.vobis.discordrpg.commands.impl;

import com.vobis.discordrpg.DiscordRPG;
import com.vobis.discordrpg.battle.BattleSimulator;
import com.vobis.discordrpg.battle.CombatInstance;
import com.vobis.discordrpg.battle.TurnInfo;
import com.vobis.discordrpg.commands.handler.ICommand;
import com.vobis.discordrpg.lang.Translations;
import com.vobis.discordrpg.mob.MobDef;
import com.vobis.discordrpg.player.Player;
import com.vobis.discordrpg.util.MessageUtils;
import com.vobis.discordrpg.util.SkillUtils;
import com.vobis.discordrpg.zones.Zone;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.stream.Collectors;

public class AttackCommand implements ICommand {

    @Override
    public Mono<?> execute(List<String> args, Message message, Member member, Guild guild, TextChannel textChannel) {

        Zone zone = DiscordRPG.INSTANCE.getZoneMap().getZone(textChannel.getName());

        if (args.isEmpty()) {
            return MessageUtils.createAutoDeleteMessage(Translations.templateFor("attack.what", member.getMention(), getMobList(zone)), textChannel, 5000);
        }

        String mob = String.join(" ", args);

        if (!isMobValid(mob, zone)) {
            return MessageUtils.createAutoDeleteMessage(Translations.templateFor("attack.invalidMob", member.getMention(), getMobList(zone)), textChannel, 5000);
        }

        MobDef enemy = DiscordRPG.INSTANCE.getMobs().getMob(mob);
        Player player = DiscordRPG.INSTANCE.getPlayers().getPlayer(member);

        CombatInstance m1 = new CombatInstance(player);
        CombatInstance m2 = new CombatInstance(enemy);

        BattleSimulator battleSimulator = new BattleSimulator();

        Mono<Message> embed = textChannel.createEmbed(spec -> setEmbedSpec(null, m1, m2, spec)).cache();

        return battleSimulator.simulate(m1, m2)
                .flatMap(turnInfo -> embed.map(msg -> Tuples.of(turnInfo, msg)))
                .flatMap(tuple -> tuple.getT2().edit(msg -> msg.setEmbed(spec -> setEmbedSpec(tuple.getT1(), m1, m2, spec))).thenReturn(tuple))
                .last()
                .flatMap(tuple -> {
                    if (tuple.getT1().getAttacker().equals(m1)) {
                        return SkillUtils.addPlayerXP(player, "strength", (int) enemy.getHealth() * 10, textChannel)
                                .and(SkillUtils.addPlayerXP(player, "defence", (int) enemy.getStrength() * 10, textChannel))
                                .and(SkillUtils.addPlayerXP(player, "hitpoints", (int) enemy.getStrength() * 10, textChannel))
                                .then(MessageUtils.createAutoDeleteMessage(m1.getFightable().getName() + " killed the " + m2.getFightable().getName(), textChannel, 5000))
                                .thenReturn(tuple.getT2());
                    } else {
                        return MessageUtils.createAutoDeleteMessage(m1.getFightable().getName() + " was killed by the " + m2.getFightable().getName(), textChannel, 5000)
                                .then(DiscordRPG.INSTANCE.getPlayers().die(textChannel, member)).thenReturn(tuple.getT2());
                    }
                })
                .flatMap(Message::delete);
    }

    private void setEmbedSpec(@Nullable TurnInfo turnInfo, CombatInstance m1, CombatInstance m2, EmbedCreateSpec spec) {
        spec.setTitle(Translations.templateFor("attack.battleTitle", m1.getFightable().getName(), m2.getFightable().getName()));
        spec.addField(m1.getFightable().getName(), getHPString(m1), true);
        spec.addField(m2.getFightable().getName(), getHPString(m2), true);

        if (turnInfo != null) {
            spec.setFooter(turnInfo.getAttacker().getFightable().getName() + " attacked for " + turnInfo.getDamage() + " damage!", null);
        }
    }

    private String getHPString(CombatInstance mobInstance) {
        if (mobInstance.isDead()) {
            return "DEAD";
        } else {
            return ((int) mobInstance.getCurrentHealth()) + " HP";
        }
    }

    private String getMobList(Zone zone) {
        return zone.getMobs().stream()
                .map(MobDef::getName)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
    }

    private boolean isMobValid(String mob, Zone zone) {
        return zone.getMobs().stream().anyMatch(m -> m.getName().toLowerCase().equals(mob.toLowerCase()));
    }
}
