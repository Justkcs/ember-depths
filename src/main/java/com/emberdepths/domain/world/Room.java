package com.emberdepths.domain.world;

import com.emberdepths.domain.item.Item;
import com.emberdepths.domain.monster.Monster;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int floor;
    private final int number;
    private final RoomType type;
    private final Monster monster;
    private final Item treasure;
    private final int gold;
    private boolean visited;

    public Room(int floor, int number, RoomType type, Monster monster, Item treasure, int gold) {
        if (floor < 1 || number < 1) {
            throw new IllegalArgumentException("Floor and room number must start at 1");
        }
        Objects.requireNonNull(type, "type");
        if (type.hasMonster() && monster == null) {
            throw new IllegalArgumentException("A " + type + " room needs a monster");
        }
        if (!type.hasMonster() && monster != null) {
            throw new IllegalArgumentException("Only monster rooms may contain a monster");
        }
        if (gold < 0) {
            throw new IllegalArgumentException("Gold must not be negative");
        }
        this.floor = floor;
        this.number = number;
        this.type = type;
        this.monster = monster;
        this.treasure = treasure;
        this.gold = gold;
    }

    public static Room empty(int floor, int number, RoomType type) {
        return new Room(floor, number, type, null, null, 0);
    }

    public void markVisited() {
        visited = true;
    }

    public int getFloor() {
        return floor;
    }

    public int getNumber() {
        return number;
    }

    public RoomType getType() {
        return type;
    }

    public Optional<Monster> getMonster() {
        return Optional.ofNullable(monster);
    }

    public Optional<Item> getTreasure() {
        return Optional.ofNullable(treasure);
    }

    public int getGold() {
        return gold;
    }

    public boolean isVisited() {
        return visited;
    }
}
