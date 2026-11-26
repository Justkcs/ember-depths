package com.emberdepths.domain.monster;

import com.emberdepths.domain.Creature;
import com.emberdepths.domain.hero.Hero;

import java.util.Random;

public abstract class Monster extends Creature {

    private static final long serialVersionUID = 1L;

    private final int experienceReward;
    private final int goldReward;

    protected Monster(String name, int maxHealth, int attack, int defense, int experienceReward, int goldReward) {
        super(name, maxHealth, attack, defense);
        if (experienceReward < 0 || goldReward < 0) {
            throw new IllegalArgumentException("Rewards must not be negative");
        }
        this.experienceReward = experienceReward;
        this.goldReward = goldReward;
    }

    public abstract AttackOutcome attack(Hero target, Random random);

    public abstract MonsterType getType();

    public boolean isCharging() {
        return false;
    }

    protected int rollDamage(Random random) {
        return Math.max(1, getAttack() - 2 + random.nextInt(5));
    }

    public boolean isBoss() {
        return false;
    }

    public int getExperienceReward() {
        return experienceReward;
    }

    public int getGoldReward() {
        return goldReward;
    }

    @Override
    public String getTypeName() {
        return getName();
    }
}
