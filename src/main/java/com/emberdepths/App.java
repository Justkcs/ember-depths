package com.emberdepths;

import com.emberdepths.persistence.GameRepository;
import com.emberdepths.persistence.SerializedGameRepository;
import com.emberdepths.persistence.TextScoreRepository;
import com.emberdepths.service.ScoreService;
import com.emberdepths.ui.ConsoleInput;
import com.emberdepths.ui.ConsoleUI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        Path dataDirectory = Paths.get(System.getProperty("ember.data", "saves"));
        GameRepository gameRepository = new SerializedGameRepository(dataDirectory.resolve("savegame.dat"));
        ScoreService scoreService = new ScoreService(new TextScoreRepository(dataDirectory.resolve("scores.txt")));
        ConsoleInput input = new ConsoleInput(System.in, System.out);
        new ConsoleUI(input, System.out, gameRepository, scoreService, new Random()).run();
    }
}
