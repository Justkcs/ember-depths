package com.emberdepths.persistence;

import com.emberdepths.domain.GameState;
import com.emberdepths.domain.ScoreEntry;
import com.emberdepths.domain.hero.Rogue;
import com.emberdepths.domain.world.Dungeon;
import com.emberdepths.domain.world.Room;
import com.emberdepths.domain.world.RoomType;
import com.emberdepths.exception.PersistenceException;
import com.emberdepths.service.ScoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void savesAndRestoresGameState() throws Exception {
        GameRepository repository = new SerializedGameRepository(tempDir.resolve("save.dat"));
        Rogue hero = new Rogue("Shade");
        hero.addGold(42);
        repository.save(new GameState(hero, new Dungeon(List.of(Room.empty(1, 1, RoomType.EMPTY)))));
        GameState loaded = repository.load();
        assertEquals("Shade", loaded.getHero().getName());
        assertEquals(42, loaded.getHero().getGold());
        repository.delete();
        assertFalse(repository.hasSave());
    }

    @Test
    void corruptedSaveRaisesPersistenceException() throws Exception {
        Path file = tempDir.resolve("broken.dat");
        Files.writeString(file, "not a save file");
        assertThrows(PersistenceException.class, () -> new SerializedGameRepository(file).load());
    }

    @Test
    void scoreboardKeepsTopEntriesAndSkipsBadLines() throws Exception {
        Path file = tempDir.resolve("scores.txt");
        Files.writeString(file, "garbage line\n");
        TextScoreRepository repository = new TextScoreRepository(file);
        ScoreService service = new ScoreService(repository);
        for (int i = 0; i < 12; i++) {
            service.record(new ScoreEntry("Hero" + i, "Mage", 1, 1, i * 10, false));
        }
        List<ScoreEntry> board = service.getLeaderboard(ScoreService.byScore());
        assertEquals(ScoreService.MAX_ENTRIES, board.size());
        assertEquals(110, board.get(0).getScore());
        assertTrue(Files.readAllLines(file).stream().noneMatch(line -> line.startsWith("garbage")));
    }
}
