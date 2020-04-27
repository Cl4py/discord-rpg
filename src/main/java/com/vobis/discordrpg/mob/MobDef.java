package com.vobis.discordrpg.mob;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vobis.discordrpg.battle.IFightable;
import com.vobis.discordrpg.stats.Stats;
import lombok.Data;

@Data
public class MobDef implements IFightable {
    private String name;
    private String description;
    private Stats stats;

    @JsonIgnore
    private String keyName;

    @Override
    public float getHealth() {
        return stats.getHealth();
    }

    @Override
    public float getStrength() {
        return stats.getStrength();
    }

    @Override
    public float getDefence() {
        return stats.getDefence();
    }
}
