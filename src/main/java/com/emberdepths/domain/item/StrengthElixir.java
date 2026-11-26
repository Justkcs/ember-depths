package com.emberdepths.domain.item;

import com.emberdepths.domain.hero.Hero;

public class StrengthElixir extends Item {

    private static final long serialVersionUID = 1L;
    public static final int ATTACK_BONUS = 2;

    public StrengthElixir() {
        super("Strength Elixir", 60);
    }

    @Override
    public String use(Hero hero) {
        hero.boostAttack(ATTACK_BONUS);
        return hero.getName() + " feels stronger! Attack permanently increased to " + hero.getAttack() + ".";
    }

    @Override
    public String getDescription() {
        return "+" + ATTACK_BONUS + " attack permanently";
    }
}
