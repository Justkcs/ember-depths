package com.emberdepths.domain.monster;

import com.emberdepths.domain.hero.Hero;

import java.util.Random;

public class Orc extends Monster {

    private static final long serialVersionUID = 1L;
    private static final int CRUSH_CHANCE = 25;

    public Orc(int floor) {
        super("Orc Brute", 40 + 6 * floor, 9 + floor + floor / 2, 3 + floor / 2, 22 + 6 * floor, 8 + 4 * floor);
    }

    @Override
    public AttackOutcome attack(Hero target, Random random) {
        if (random.nextInt(100) < CRUSH_CHANCE) {
            int damage = target.receiveDamage(rollDamage(random) * 8 / 5);
            return new AttackOutcome(damage, "The Orc Brute lands a crushing blow for " + damage + " damage!");
        }
        int damage = target.receiveDamage(rollDamage(random));
        return new AttackOutcome(damage, "The Orc Brute swings its axe for " + damage + " damage.");
    }

    @Override
    public MonsterType getType() {
        return MonsterType.ORC;
    }
}
