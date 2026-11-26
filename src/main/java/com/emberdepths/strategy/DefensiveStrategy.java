package com.emberdepths.strategy;

import java.util.Random;

public class DefensiveStrategy implements AttackStrategy {

    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
        return "Defensive";
    }

    @Override
    public String getDescription() {
        return "-30% damage, double defense";
    }

    @Override
    public int calculateDamage(int attack, Random random) {
        return Math.max(1, attack * 7 / 10 - 1 + random.nextInt(3));
    }

    @Override
    public int adjustDefense(int defense) {
        return defense * 2;
    }
}
