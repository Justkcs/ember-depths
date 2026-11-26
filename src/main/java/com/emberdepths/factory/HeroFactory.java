package com.emberdepths.factory;

import com.emberdepths.domain.hero.Hero;
import com.emberdepths.domain.hero.HeroClass;
import com.emberdepths.domain.hero.Mage;
import com.emberdepths.domain.hero.Rogue;
import com.emberdepths.domain.hero.Warrior;

import java.util.Objects;

public class HeroFactory {

    public Hero create(HeroClass heroClass, String name) {
        Objects.requireNonNull(heroClass, "heroClass");
        return switch (heroClass) {
            case WARRIOR -> new Warrior(name);
            case MAGE -> new Mage(name);
            case ROGUE -> new Rogue(name);
        };
    }
}
