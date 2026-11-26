package com.emberdepths.factory;

import com.emberdepths.domain.monster.Dragon;
import com.emberdepths.domain.monster.Goblin;
import com.emberdepths.domain.monster.Monster;
import com.emberdepths.domain.monster.MonsterType;
import com.emberdepths.domain.monster.Orc;
import com.emberdepths.domain.monster.Skeleton;

import java.util.Objects;
import java.util.Random;

public class MonsterFactory {

    private final Random random;

    public MonsterFactory(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public Monster create(MonsterType type, int floor) {
        Objects.requireNonNull(type, "type");
        if (floor < 1) {
            throw new IllegalArgumentException("Floor must be at least 1");
        }
        return switch (type) {
            case GOBLIN -> new Goblin(floor);
            case SKELETON -> new Skeleton(floor);
            case ORC -> new Orc(floor);
            case DRAGON -> new Dragon();
        };
    }

    public Monster createForFloor(int floor) {
        return create(chooseType(floor), floor);
    }

    public Monster createBoss() {
        return create(MonsterType.DRAGON, 1);
    }

    private MonsterType chooseType(int floor) {
        int roll = random.nextInt(100);
        if (floor >= 4 && roll < 35) {
            return MonsterType.ORC;
        }
        if (floor >= 2 && roll < 70) {
            return MonsterType.SKELETON;
        }
        return MonsterType.GOBLIN;
    }
}
