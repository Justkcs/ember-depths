package com.emberdepths.domain.hero;

import com.emberdepths.domain.Creature;

import java.util.Random;

public class Warrior extends Hero {

    private static final long serialVersionUID = 1L;

    public Warrior(String name) {
        super(name, 120, 14, 8, 30);
    }

    @Override
    protected SkillResult performSkill(Creature target, Random random) {
        int damage = target.receiveDamage(rollAttackDamage(random) * 2);
        return new SkillResult(damage, getName() + " spins into a Whirlwind Strike for " + damage + " damage!");
    }

    @Override
    public String getSkillName() {
        return "Whirlwind Strike";
    }

    @Override
    public int getSkillCost() {
        return 10;
    }

    @Override
    public HeroClass getHeroClass() {
        return HeroClass.WARRIOR;
    }

    @Override
    protected void applyLevelUpBonus() {
        increaseMaxHealth(20);
        increaseAttack(3);
        increaseDefense(2);
        increaseMaxMana(5);
    }
}
