package com.vobis.discordrpg.battle;

import lombok.Data;

@Data
public class TurnInfo {
    private final CombatInstance attacker;
    private final float damage;
    private final boolean won;
}
