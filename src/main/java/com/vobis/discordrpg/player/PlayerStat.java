package com.vobis.discordrpg.player;

import com.vobis.discordrpg.util.MathUtil;
import lombok.Data;

@Data
public class PlayerStat {

    private static final float XP_FACTOR = 3.5F;
    private static final float LEVEL_FACTOR = 1F / XP_FACTOR;

    private final String name;
    private int xp;

    public int getLevel() {
        return MathUtil.floor(MathUtil.pow(xp, LEVEL_FACTOR));
    }

    /**
     * @return A boolean representing whether the stat's level has gone up or not
     * */
    public boolean addXP(int xp) {
        int oldLevel = getLevel();
        this.xp += xp;
        return getLevel() > oldLevel;
    }

    public void setLevel(int level) {
        this.xp = MathUtil.floor(MathUtil.pow(level, XP_FACTOR));
    }
}
