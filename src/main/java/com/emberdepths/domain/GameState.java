package com.emberdepths.domain;

import com.emberdepths.domain.hero.Hero;
import com.emberdepths.domain.monster.Monster;
import com.emberdepths.domain.world.Dungeon;

import java.io.Serializable;
import java.util.Objects;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Hero hero;
    private final Dungeon dungeon;
    private int monstersDefeated;
    private boolean bossDefeated;

    public GameState(Hero hero, Dungeon dungeon) {
        this.hero = Objects.requireNonNull(hero, "hero");
        this.dungeon = Objects.requireNonNull(dungeon, "dungeon");
    }

    public void recordMonsterDefeated(Monster monster) {
        Objects.requireNonNull(monster, "monster");
        if (monster.isAlive()) {
            throw new IllegalArgumentException("Only defeated monsters can be recorded");
        }
        monstersDefeated++;
        if (monster.isBoss()) {
            bossDefeated = true;
        }
    }

    public Hero getHero() {
        return hero;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public int getMonstersDefeated() {
        return monstersDefeated;
    }

    public boolean isBossDefeated() {
        return bossDefeated;
    }
}
