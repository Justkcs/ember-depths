package com.emberdepths.domain.monster;

import com.emberdepths.domain.hero.Hero;

import java.util.Random;

public class Dragon extends Monster {

    private static final long serialVersionUID = 1L;
    private static final int BREATH_INTERVAL = 3;

    private int turnCounter;

    public Dragon() {
        super("Ember Dragon", 200, 24, 10, 300, 200);
    }

    @Override
    public AttackOutcome attack(Hero target, Random random) {
        turnCounter++;
        if (turnCounter % BREATH_INTERVAL == 0) {
            int damage = target.receiveTrueDamage(getAttack() * 3 / 2);
            return new AttackOutcome(damage, "The Ember Dragon unleashes a torrent of fire for " + damage + " damage!");
        }
        int damage = target.receiveDamage(rollDamage(random));
        String warning = turnCounter % BREATH_INTERVAL == BREATH_INTERVAL - 1
                ? " Its chest begins to glow..."
                : "";
        return new AttackOutcome(damage, "The Ember Dragon rakes you with its claws for " + damage + " damage." + warning);
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public MonsterType getType() {
        return MonsterType.DRAGON;
    }

    @Override
    public boolean isCharging() {
        return turnCounter % BREATH_INTERVAL == BREATH_INTERVAL - 1;
    }

    public int getTurnCounter() {
        return turnCounter;
    }
}
