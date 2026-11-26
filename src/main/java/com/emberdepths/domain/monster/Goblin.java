package com.emberdepths.domain.monster;

import com.emberdepths.domain.hero.Hero;

import java.util.Random;

public class Goblin extends Monster {

    private static final long serialVersionUID = 1L;
    private static final int DOUBLE_STRIKE_CHANCE = 35;

    public Goblin(int floor) {
        super("Goblin", 28 + 6 * floor, 6 + floor, 1 + floor / 2, 12 + 5 * floor, 4 + 3 * floor);
    }

    @Override
    public AttackOutcome attack(Hero target, Random random) {
        int first = target.receiveDamage(rollDamage(random));
        if (random.nextInt(100) < DOUBLE_STRIKE_CHANCE) {
            int second = target.receiveDamage(rollDamage(random));
            return new AttackOutcome(first + second,
                    "The Goblin darts in and stabs twice for " + first + " and " + second + " damage!");
        }
        return new AttackOutcome(first, "The Goblin stabs you for " + first + " damage.");
    }

    @Override
    public MonsterType getType() {
        return MonsterType.GOBLIN;
    }
}
