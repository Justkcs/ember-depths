package com.emberdepths.domain.item;

import com.emberdepths.domain.hero.Hero;

public class HealthPotion extends Item {

    private static final long serialVersionUID = 1L;
    public static final int HEAL_AMOUNT = 45;

    public HealthPotion() {
        super("Health Potion", 25);
    }

    @Override
    public String use(Hero hero) {
        int healed = hero.heal(HEAL_AMOUNT);
        return hero.getName() + " drinks a Health Potion and recovers " + healed + " HP.";
    }

    @Override
    public String getDescription() {
        return "restores " + HEAL_AMOUNT + " HP";
    }
}
