package com.emberdepths.domain.hero;

import com.emberdepths.domain.Creature;

import java.util.Random;

public class Rogue extends Hero {

    private static final long serialVersionUID = 1L;
    private static final int BACKSTAB_CHANCE = 65;

    public Rogue(String name) {
        super(name, 100, 12, 6, 40);
    }

    @Override
    protected SkillResult performSkill(Creature target, Random random) {
        if (random.nextInt(100) < BACKSTAB_CHANCE) {
            int damage = target.receiveDamage(rollAttackDamage(random) * 3);
            return new SkillResult(damage, getName() + " strikes from the shadows with a Backstab for " + damage + " damage!");
        }
        return new SkillResult(0, getName() + " lunges for a Backstab but the target turns in time.");
    }

    @Override
    public String getSkillName() {
        return "Backstab";
    }

    @Override
    public int getSkillCost() {
        return 8;
    }

    @Override
    public int getFleeChance() {
        return 80;
    }

    @Override
    public HeroClass getHeroClass() {
        return HeroClass.ROGUE;
    }

    @Override
    protected void applyLevelUpBonus() {
        increaseMaxHealth(15);
        increaseAttack(3);
        increaseDefense(2);
        increaseMaxMana(5);
    }
}
