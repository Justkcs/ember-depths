package com.emberdepths.domain.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Dungeon implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<Room> rooms;
    private int currentIndex;

    public Dungeon(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("A dungeon needs at least one room");
        }
        this.rooms = new ArrayList<>(rooms);
        this.currentIndex = -1;
    }

    public boolean hasNextRoom() {
        return currentIndex < rooms.size() - 1;
    }

    public Room advance() {
        if (!hasNextRoom()) {
            throw new IllegalStateException("There are no rooms left to explore");
        }
        currentIndex++;
        Room room = rooms.get(currentIndex);
        room.markVisited();
        return room;
    }

    public Optional<Room> getCurrentRoom() {
        return currentIndex < 0 ? Optional.empty() : Optional.of(rooms.get(currentIndex));
    }

    public int getCurrentFloor() {
        return getCurrentRoom().map(Room::getFloor).orElse(1);
    }

    public int getTotalFloors() {
        return rooms.get(rooms.size() - 1).getFloor();
    }

    public int getRoomsVisited() {
        return currentIndex + 1;
    }

    public int getTotalRooms() {
        return rooms.size();
    }

    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }
}
