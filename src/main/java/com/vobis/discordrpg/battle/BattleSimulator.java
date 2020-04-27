package com.vobis.discordrpg.battle;

import reactor.core.publisher.Flux;
import reactor.function.TupleUtils;
import reactor.util.function.Tuples;

import java.time.Duration;

public class BattleSimulator {

    public static final Duration TURN_TIMER = Duration.ofMillis(1500);

    private boolean turn = true;

    public Flux<TurnInfo> simulate(CombatInstance m1, CombatInstance m2) {
        return Flux.interval(Duration.ZERO, TURN_TIMER)
                .onBackpressureDrop()
                .map($ -> Tuples.of(m1, m2))
                .map(TupleUtils.function(this::doMove))
                .takeUntil(TurnInfo::isWon);
    }

    private TurnInfo doMove(CombatInstance m1, CombatInstance m2) {
        CombatInstance attacker;
        CombatInstance victim;

        if (turn) {
            attacker = m1;
            victim = m2;
        } else {
            attacker = m2;
            victim = m1;
        }

        float damage = attacker.attack(victim);

        TurnInfo result = new TurnInfo(attacker, damage, victim.isDead());
        turn = !turn;

        return result;
    }
}
