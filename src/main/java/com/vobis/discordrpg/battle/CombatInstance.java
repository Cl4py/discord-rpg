package com.vobis.discordrpg.battle;

import com.vobis.discordrpg.util.MathUtil;
import lombok.Data;

@Data
public class CombatInstance {

    private final IFightable fightable;

    private float currentHealth;

    public CombatInstance(IFightable fightable) {
        this.fightable = fightable;
        this.currentHealth = fightable.getHealth();
    }

    public float attack(CombatInstance other) {
        float attackDamage = MathUtil.pow((fightable.getStrength() / 5), 1.4F);
        return other.damage(attackDamage);
    }

    public float damage(float amount) {
        int damage = MathUtil.floor(amount * MathUtil.clamp(1 - (MathUtil.sqrt(fightable.getDefence()) * 0.08F), 0.01F, 1.0F));

        if (damage <= 0) {
            damage = 1;
        }

        this.currentHealth -= damage;

        if (this.currentHealth <= 0) {
            this.currentHealth = 0;
        }

        return damage;
    }

    public boolean isDead() {
        return this.currentHealth <= 0F;
    }
}
