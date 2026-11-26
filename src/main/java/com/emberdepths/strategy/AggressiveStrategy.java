package com.emberdepths.strategy;

import java.util.Random;

public class AggressiveStrategy implements AttackStrategy {

    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
        return "Aggressive";
    }

    @Override
    public String getDescription() {
        return "+50% damage, half defense";
    }

    @Override
    public int calculateDamage(int attack, Random random) {
        return Math.max(1, attack * 3 / 2 - 2 + random.nextInt(5));
    }

    @Override
    public int adjustDefense(int defense) {
        return defense / 2;
    }
}
