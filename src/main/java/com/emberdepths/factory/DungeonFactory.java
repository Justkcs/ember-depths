package com.emberdepths.factory;

import com.emberdepths.domain.item.Item;
import com.emberdepths.domain.world.Dungeon;
import com.emberdepths.domain.world.Room;
import com.emberdepths.domain.world.RoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DungeonFactory {

    private final MonsterFactory monsterFactory;
    private final ItemFactory itemFactory;
    private final Random random;

    public DungeonFactory(MonsterFactory monsterFactory, ItemFactory itemFactory, Random random) {
        this.monsterFactory = Objects.requireNonNull(monsterFactory, "monsterFactory");
        this.itemFactory = Objects.requireNonNull(itemFactory, "itemFactory");
        this.random = Objects.requireNonNull(random, "random");
    }

    public Dungeon create(int floors, int roomsPerFloor) {
        if (floors < 1 || roomsPerFloor < 1) {
            throw new IllegalArgumentException("A dungeon needs at least one floor and one room per floor");
        }
        List<Room> rooms = new ArrayList<>();
        for (int floor = 1; floor <= floors; floor++) {
            for (int number = 1; number <= roomsPerFloor; number++) {
                boolean last = floor == floors && number == roomsPerFloor;
                rooms.add(last ? createBossRoom(floor, number) : createRoom(floor, number));
            }
        }
        return new Dungeon(rooms);
    }

    private Room createBossRoom(int floor, int number) {
        return new Room(floor, number, RoomType.BOSS, monsterFactory.createBoss(), null, 0);
    }

    private Room createRoom(int floor, int number) {
        int roll = random.nextInt(100);
        if (roll < 60) {
            return new Room(floor, number, RoomType.MONSTER, monsterFactory.createForFloor(floor), null, 0);
        }
        if (roll < 78) {
            Item treasure = random.nextBoolean() ? itemFactory.createRandom() : null;
            int gold = 10 + random.nextInt(15 * floor);
            return new Room(floor, number, RoomType.TREASURE, null, treasure, gold);
        }
        if (roll < 90) {
            return Room.empty(floor, number, RoomType.SPRING);
        }
        return Room.empty(floor, number, RoomType.EMPTY);
    }
}
