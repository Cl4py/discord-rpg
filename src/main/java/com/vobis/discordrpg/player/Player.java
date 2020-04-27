package com.vobis.discordrpg.player;

import com.vobis.discordrpg.battle.IFightable;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Player implements IFightable {
    private final Map<String, PlayerStat> playerStats = new HashMap<>();
    private String name;

    public Player(String name) {
        this.name = name;

        addStat(new PlayerStat("strength"));
        addStat(new PlayerStat("defence"));
        addStat(new PlayerStat("hitpoints"));
        addStat(new PlayerStat("woodcutting"));
        addStat(new PlayerStat("mining"));
        addStat(new PlayerStat("fishing"));

        getPlayerStat("hitpoints").setXp(3162);
    }

    public void addStat(PlayerStat stat) {
        playerStats.put(stat.getName(), stat);
    }

    public PlayerStat getPlayerStat(String name) {
        return playerStats.get(name);
    }

    @Override
    public float getDefence() {
        return getPlayerStat("strength").getLevel();
    }

    @Override
    public float getStrength() {
        return getPlayerStat("strength").getLevel();
    }

    @Override
    public float getHealth() {
        return getPlayerStat("hitpoints").getLevel();
    }

    @Override
    public String getName() {
        return name;
    }
}
