package com.emberdepths.factory;

import com.emberdepths.domain.GameState;
import com.emberdepths.domain.hero.Hero;
import com.emberdepths.domain.item.HealthPotion;
import com.emberdepths.domain.item.ManaPotion;

import java.util.Objects;
import java.util.Random;

public class GameFactory {

    public static final int FLOORS = 10;
    public static final int ROOMS_PER_FLOOR = 3;

    private final DungeonFactory dungeonFactory;

    public GameFactory(Random random) {
        Objects.requireNonNull(random, "random");
        this.dungeonFactory = new DungeonFactory(new MonsterFactory(random), new ItemFactory(random), random);
    }

    public GameState createNewGame(Hero hero) {
        Objects.requireNonNull(hero, "hero");
        hero.getInventory().add(new HealthPotion());
        hero.getInventory().add(new HealthPotion());
        hero.getInventory().add(new ManaPotion());
        return new GameState(hero, dungeonFactory.create(FLOORS, ROOMS_PER_FLOOR));
    }
}
