package com.emberdepths.domain.world;

public enum RoomType {

    MONSTER("a monster lair"),
    TREASURE("a treasure chamber"),
    SPRING("a glowing healing spring"),
    EMPTY("an empty corridor"),
    BOSS("the Dragon's Hoard");

    private final String description;

    RoomType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasMonster() {
        return this == MONSTER || this == BOSS;
    }
}
