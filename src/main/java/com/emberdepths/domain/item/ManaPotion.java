package com.emberdepths.domain.item;

import com.emberdepths.domain.hero.Hero;

public class ManaPotion extends Item {

    private static final long serialVersionUID = 1L;
    public static final int RESTORE_AMOUNT = 30;

    public ManaPotion() {
        super("Mana Potion", 20);
    }

    @Override
    public String use(Hero hero) {
        int restored = hero.restoreMana(RESTORE_AMOUNT);
        return hero.getName() + " drinks a Mana Potion and regains " + restored + " MP.";
    }

    @Override
    public String getDescription() {
        return "restores " + RESTORE_AMOUNT + " MP";
    }
}
