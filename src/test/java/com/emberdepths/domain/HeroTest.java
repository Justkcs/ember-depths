package com.emberdepths.domain;

import com.emberdepths.FixedRandom;
import com.emberdepths.domain.hero.Hero;
import com.emberdepths.domain.hero.Mage;
import com.emberdepths.domain.hero.Rogue;
import com.emberdepths.domain.hero.Warrior;
import com.emberdepths.domain.monster.Goblin;
import com.emberdepths.domain.monster.Monster;
import com.emberdepths.exception.InsufficientManaException;
import com.emberdepths.strategy.AggressiveStrategy;
import com.emberdepths.strategy.DefensiveStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeroTest {

    @Test
    void rejectsInvalidNames() {
        assertThrows(IllegalArgumentException.class, () -> new Warrior(""));
        assertThrows(IllegalArgumentException.class, () -> new Warrior("A"));
        assertThrows(IllegalArgumentException.class, () -> new Warrior("Bad;Name"));
        assertThrows(IllegalArgumentException.class, () -> new Warrior("ThisNameIsWayTooLong"));
    }

    @Test
    void levelsUpAndGrowsStats() {
        Hero hero = new Warrior("Aria");
        int attackBefore = hero.getAttack();
        int levels = hero.gainExperience(50);
        assertEquals(1, levels);
        assertEquals(2, hero.getLevel());
        assertEquals(attackBefore + 3, hero.getAttack());
        assertEquals(hero.getMaxHealth(), hero.getHealth());
    }

    @Test
    void skillThrowsWhenManaIsTooLow() {
        Hero hero = new Warrior("Brick");
        Monster goblin = new Goblin(10);
        FixedRandom random = new FixedRandom(0);
        assertThrows(InsufficientManaException.class, () -> {
            for (int i = 0; i < 10 && goblin.isAlive(); i++) {
                hero.useSkill(goblin, random);
            }
        });
    }

    @Test
    void eachClassOverridesTheSkillPolymorphically() throws InsufficientManaException {
        List<Hero> heroes = List.of(new Warrior("Tank"), new Mage("Sage"), new Rogue("Shade"));
        for (Hero hero : heroes) {
            Monster dummy = new Goblin(1);
            int before = dummy.getHealth();
            hero.useSkill(dummy, new FixedRandom(0));
            assertTrue(dummy.getHealth() < before, hero.getSkillName() + " should deal damage");
        }
    }

    @Test
    void strategyChangesDamageAtRuntime() {
        Hero hero = new Warrior("Brick");
        FixedRandom random = new FixedRandom(2);
        int balanced = hero.rollAttackDamage(random);
        hero.setStrategy(new AggressiveStrategy());
        int aggressive = hero.rollAttackDamage(random);
        hero.setStrategy(new DefensiveStrategy());
        int defensive = hero.rollAttackDamage(random);
        assertTrue(aggressive > balanced);
        assertTrue(defensive < balanced);
    }

    @Test
    void rogueFleesMoreEasilyThanWarrior() {
        assertTrue(new Rogue("Shade").getFleeChance() > new Warrior("Tank").getFleeChance());
    }
}
