package com.emberdepths.domain.hero;

import com.emberdepths.domain.Creature;

import java.util.Random;

public class Mage extends Hero {

    private static final long serialVersionUID = 1L;

    public Mage(String name) {
        super(name, 85, 11, 4, 60);
    }

    @Override
    protected SkillResult performSkill(Creature target, Random random) {
        int damage = target.receiveTrueDamage(getAttack() * 2 + random.nextInt(10));
        return new SkillResult(damage, getName() + " hurls a Fireball that burns through armor for " + damage + " damage!");
    }

    @Override
    public String getSkillName() {
        return "Fireball";
    }

    @Override
    public int getSkillCost() {
        return 15;
    }

    @Override
    public HeroClass getHeroClass() {
        return HeroClass.MAGE;
    }

    @Override
    protected void applyLevelUpBonus() {
        increaseMaxHealth(12);
        increaseAttack(4);
        increaseDefense(1);
        increaseMaxMana(15);
    }
}
