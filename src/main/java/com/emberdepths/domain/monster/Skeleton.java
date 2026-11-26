package com.emberdepths.domain.monster;

import com.emberdepths.domain.hero.Hero;

import java.util.Random;

public class Skeleton extends Monster {

    private static final long serialVersionUID = 1L;
    private static final int PIERCING_CHANCE = 30;

    public Skeleton(int floor) {
        super("Skeleton Archer", 30 + 6 * floor, 7 + floor, 2 + floor / 2, 15 + 5 * floor, 5 + 3 * floor);
    }

    @Override
    public AttackOutcome attack(Hero target, Random random) {
        if (random.nextInt(100) < PIERCING_CHANCE) {
            int damage = target.receiveTrueDamage(getAttack());
            return new AttackOutcome(damage, "A piercing bone arrow ignores your armor: " + damage + " damage!");
        }
        int damage = target.receiveDamage(rollDamage(random));
        return new AttackOutcome(damage, "The Skeleton Archer shoots you for " + damage + " damage.");
    }

    @Override
    public MonsterType getType() {
        return MonsterType.SKELETON;
    }
}
