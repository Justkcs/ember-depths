package com.emberdepths.strategy;

import java.util.Random;

public class BalancedStrategy implements AttackStrategy {

    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
        return "Balanced";
    }

    @Override
    public String getDescription() {
        return "normal damage, normal defense";
    }

    @Override
    public int calculateDamage(int attack, Random random) {
        return Math.max(1, attack - 2 + random.nextInt(5));
    }

    @Override
    public int adjustDefense(int defense) {
        return defense;
    }
}
