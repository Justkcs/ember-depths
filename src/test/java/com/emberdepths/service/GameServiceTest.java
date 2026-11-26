package com.emberdepths.service;

import com.emberdepths.FixedRandom;
import com.emberdepths.domain.GameState;
import com.emberdepths.domain.hero.Warrior;
import com.emberdepths.domain.monster.Goblin;
import com.emberdepths.domain.world.Dungeon;
import com.emberdepths.domain.world.Room;
import com.emberdepths.domain.world.RoomType;
import com.emberdepths.service.event.EventType;
import com.emberdepths.service.event.GameEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameServiceTest {

    private GameService serviceWithRooms(Room... rooms) {
        GameState state = new GameState(new Warrior("Tester"), new Dungeon(List.of(rooms)));
        return new GameService(state, new FixedRandom(0));
    }

    @Test
    void monsterRoomStartsCombatAndObserversAreNotified() {
        GameService game = serviceWithRooms(new Room(1, 1, RoomType.MONSTER, new Goblin(1), null, 0));
        List<GameEvent> events = new ArrayList<>();
        game.addListener(events::add);
        game.enterNextRoom();
        assertTrue(game.isInCombat());
        assertTrue(events.stream().anyMatch(e -> e.type() == EventType.COMBAT));
    }

    @Test
    void defeatingMonsterGivesRewards() {
        GameService game = serviceWithRooms(new Room(1, 1, RoomType.MONSTER, new Goblin(1), null, 0),
                Room.empty(1, 2, RoomType.EMPTY));
        game.enterNextRoom();
        while (game.isInCombat()) {
            game.attack();
        }
        assertEquals(1, game.getState().getMonstersDefeated());
        assertTrue(game.getState().getHero().getGold() > 0);
        assertFalse(game.isGameOver());
    }

    @Test
    void cannotAttackOutsideCombat() {
        GameService game = serviceWithRooms(Room.empty(1, 1, RoomType.EMPTY));
        assertThrows(IllegalStateException.class, game::attack);
    }

    @Test
    void springHealsTheHero() {
        GameService game = serviceWithRooms(Room.empty(1, 1, RoomType.SPRING));
        Warrior hero = (Warrior) game.getState().getHero();
        hero.receiveTrueDamage(60);
        game.enterNextRoom();
        assertEquals(hero.getMaxHealth() - 60 + hero.getMaxHealth() * 4 / 10, hero.getHealth());
    }
}
