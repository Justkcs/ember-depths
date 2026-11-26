package com.emberdepths.ui;

import com.emberdepths.domain.GameState;
import com.emberdepths.domain.ScoreEntry;
import com.emberdepths.domain.hero.Hero;
import com.emberdepths.domain.hero.HeroClass;
import com.emberdepths.domain.item.Inventory;
import com.emberdepths.domain.item.Item;
import com.emberdepths.domain.monster.Monster;
import com.emberdepths.domain.world.Dungeon;
import com.emberdepths.exception.InputClosedException;
import com.emberdepths.exception.InsufficientManaException;
import com.emberdepths.exception.PersistenceException;
import com.emberdepths.factory.GameFactory;
import com.emberdepths.factory.HeroFactory;
import com.emberdepths.persistence.GameRepository;
import com.emberdepths.service.GameService;
import com.emberdepths.service.ScoreService;
import com.emberdepths.service.event.GameEvent;
import com.emberdepths.service.event.GameEventListener;
import com.emberdepths.strategy.AggressiveStrategy;
import com.emberdepths.strategy.AttackStrategy;
import com.emberdepths.strategy.BalancedStrategy;
import com.emberdepths.strategy.DefensiveStrategy;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ConsoleUI implements GameEventListener {

    private static final int FLOORS = GameFactory.FLOORS;
    private static final int ROOMS_PER_FLOOR = GameFactory.ROOMS_PER_FLOOR;
    private static final int BAR_WIDTH = 20;

    private final ConsoleInput input;
    private final PrintStream out;
    private final GameRepository gameRepository;
    private final ScoreService scoreService;
    private final Random random;
    private final HeroFactory heroFactory = new HeroFactory();
    private final GameFactory gameFactory;
    private final List<AttackStrategy> strategies =
            List.of(new BalancedStrategy(), new AggressiveStrategy(), new DefensiveStrategy());

    public ConsoleUI(ConsoleInput input, PrintStream out, GameRepository gameRepository,
                     ScoreService scoreService, Random random) {
        this.input = Objects.requireNonNull(input, "input");
        this.out = Objects.requireNonNull(out, "out");
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository");
        this.scoreService = Objects.requireNonNull(scoreService, "scoreService");
        this.random = Objects.requireNonNull(random, "random");
        this.gameFactory = new GameFactory(random);
    }

    public void run() {
        printBanner();
        try {
            boolean running = true;
            while (running) {
                out.println();
                out.println("=========== MAIN MENU ===========");
                out.println("  1) New adventure");
                out.println("  2) Continue saved adventure" + (gameRepository.hasSave() ? "" : " (none)"));
                out.println("  3) Hall of Fame");
                out.println("  4) How to play");
                out.println("  5) Exit");
                switch (input.readChoice("> ", 1, 5)) {
                    case 1 -> startNewGame();
                    case 2 -> continueGame();
                    case 3 -> showLeaderboard();
                    case 4 -> showHelp();
                    default -> running = false;
                }
            }
        } catch (InputClosedException e) {
            out.println();
            out.println("Input closed.");
        }
        out.println("Farewell, adventurer!");
    }

    private void printBanner() {
        out.println("  _____           _                  ____             _   _         ");
        out.println(" | ____|_ __ ___ | |__   ___ _ __   |  _ \\  ___ _ __ | |_| |__  ___ ");
        out.println(" |  _| | '_ ` _ \\| '_ \\ / _ \\ '__|  | | | |/ _ \\ '_ \\| __| '_ \\/ __|");
        out.println(" | |___| | | | | | |_) |  __/ |     | |_| |  __/ |_) | |_| | | \\__ \\");
        out.println(" |_____|_| |_| |_|_.__/ \\___|_|     |____/ \\___| .__/ \\__|_| |_|___/");
        out.println("                                               |_|                  ");
        out.println("   Descend " + FLOORS + " floors and defeat the Ember Dragon.");
    }

    private void showHelp() {
        out.println();
        out.println("HOW TO PLAY");
        out.println("  - The dungeon has " + FLOORS + " floors with " + ROOMS_PER_FLOOR + " rooms each.");
        out.println("  - Rooms hold monsters, treasure, healing springs or nothing at all.");
        out.println("  - The Ember Dragon waits in the very last room. It cannot be escaped.");
        out.println("  - Combat styles: Balanced, Aggressive (more damage, less defense),");
        out.println("    Defensive (less damage, more defense). Switching style is a free action.");
        out.println("  - Each class has a unique skill that costs mana.");
        out.println("  - Save outside of combat. Your best runs are kept in the Hall of Fame.");
        out.println("  - Prefer graphics? Start the game with the --gui option for the window version.");
    }

    private void startNewGame() {
        if (gameRepository.hasSave()) {
            out.println("Starting a new adventure will overwrite your saved game.");
            if (input.readChoice("1) Continue  2) Cancel  > ", 1, 2) == 2) {
                return;
            }
        }
        Hero hero = createHero();
        GameState state = gameFactory.createNewGame(hero);
        out.println();
        out.println(hero.getName() + " the " + hero.getTypeName() + " lights a torch and enters the Ember Depths...");
        play(state);
    }

    private Hero createHero() {
        out.println();
        out.println("Choose your class:");
        HeroClass[] classes = HeroClass.values();
        for (int i = 0; i < classes.length; i++) {
            out.printf("  %d) %-8s %s%n", i + 1, classes[i].getDisplayName(), classes[i].getDescription());
        }
        HeroClass chosen = classes[input.readChoice("> ", 1, classes.length) - 1];
        while (true) {
            String name = input.readLine("Name your hero (2-16 letters or digits): ");
            try {
                return heroFactory.create(chosen, name);
            } catch (IllegalArgumentException e) {
                out.println("  ! " + e.getMessage());
            }
        }
    }

    private void continueGame() {
        if (!gameRepository.hasSave()) {
            out.println("There is no saved adventure yet.");
            return;
        }
        try {
            GameState state = gameRepository.load();
            out.println("Welcome back, " + state.getHero().getName() + "!");
            play(state);
        } catch (PersistenceException e) {
            out.println("  ! " + e.getMessage());
        }
    }

    private void play(GameState state) {
        GameService game = new GameService(state, random);
        game.addListener(this);
        while (!game.isGameOver()) {
            if (game.isInCombat()) {
                combatTurn(game);
            } else if (!explorationTurn(game)) {
                return;
            }
        }
        finishRun(game);
    }

    private boolean explorationTurn(GameService game) {
        Hero hero = game.getState().getHero();
        Dungeon dungeon = game.getState().getDungeon();
        out.println();
        out.printf("------ Floor %d/%d | Rooms explored %d/%d ------%n", dungeon.getCurrentFloor(),
                dungeon.getTotalFloors(), dungeon.getRoomsVisited(), dungeon.getTotalRooms());
        printHeroSummary(hero);
        out.println("  1) Go deeper   2) Inventory   3) Combat style");
        out.println("  4) Hero sheet  5) Save game   6) Save and return to menu");
        switch (input.readChoice("> ", 1, 6)) {
            case 1 -> game.enterNextRoom();
            case 2 -> inventoryMenu(game);
            case 3 -> strategyMenu(game);
            case 4 -> printHeroSheet(game);
            case 5 -> saveGame(game.getState());
            default -> {
                saveGame(game.getState());
                return false;
            }
        }
        return true;
    }

    private void combatTurn(GameService game) {
        Hero hero = game.getState().getHero();
        Monster enemy = game.getCurrentEnemy();
        out.println();
        out.printf("  %-16s HP %s %d/%d%n", enemy.getName(), bar(enemy.getHealth(), enemy.getMaxHealth()),
                enemy.getHealth(), enemy.getMaxHealth());
        printHeroSummary(hero);
        out.printf("  1) Attack  2) %s (%d MP)  3) Use item  4) Combat style  5) Flee%n",
                hero.getSkillName(), hero.getSkillCost());
        switch (input.readChoice("> ", 1, 5)) {
            case 1 -> game.attack();
            case 2 -> useSkill(game);
            case 3 -> inventoryMenu(game);
            case 4 -> strategyMenu(game);
            default -> game.flee();
        }
    }

    private void useSkill(GameService game) {
        try {
            game.useSkill();
        } catch (InsufficientManaException e) {
            out.println("  ! " + e.getMessage());
        }
    }

    private void inventoryMenu(GameService game) {
        Inventory<Item> inventory = game.getState().getHero().getInventory();
        while (true) {
            out.println();
            out.printf("BAG (%d/%d), total value %d gold%n", inventory.size(), inventory.getCapacity(),
                    inventory.totalValue());
            if (inventory.isEmpty()) {
                out.println("  Your bag is empty.");
                return;
            }
            List<Item> items = inventory.getItems();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                out.printf("  %d) %-16s %-28s %3d gold%n", i + 1, item.getName(), item.getDescription(), item.getValue());
            }
            out.println("  1) Use an item  2) Sort by name  3) Sort by value  4) Back");
            switch (input.readChoice("> ", 1, 4)) {
                case 1 -> {
                    int index = input.readChoice("Item number (0 to cancel): ", 0, items.size());
                    if (index > 0) {
                        game.useItem(index - 1);
                        return;
                    }
                }
                case 2 -> inventory.sort(Comparator.comparing(Item::getName));
                case 3 -> inventory.sort(Comparator.comparingInt(Item::getValue).reversed());
                default -> {
                    return;
                }
            }
        }
    }

    private void strategyMenu(GameService game) {
        Hero hero = game.getState().getHero();
        out.println();
        out.println("Choose a combat style (current: " + hero.getStrategy().getName() + "):");
        for (int i = 0; i < strategies.size(); i++) {
            AttackStrategy strategy = strategies.get(i);
            out.printf("  %d) %-11s %s%n", i + 1, strategy.getName(), strategy.getDescription());
        }
        game.changeStrategy(strategies.get(input.readChoice("> ", 1, strategies.size()) - 1));
    }

    private void printHeroSummary(Hero hero) {
        out.printf("  %-16s HP %s %d/%d  MP %d/%d  [%s]%n", hero.getName(), bar(hero.getHealth(), hero.getMaxHealth()),
                hero.getHealth(), hero.getMaxHealth(), hero.getMana(), hero.getMaxMana(), hero.getStrategy().getName());
    }

    private void printHeroSheet(GameService game) {
        Hero hero = game.getState().getHero();
        out.println();
        out.println("=== " + hero.getName() + " the " + hero.getTypeName() + " ===");
        out.printf("  Level %d  (XP %d/%d)%n", hero.getLevel(), hero.getExperience(), hero.getExperienceToNextLevel());
        out.printf("  Health %d/%d  Mana %d/%d%n", hero.getHealth(), hero.getMaxHealth(), hero.getMana(), hero.getMaxMana());
        out.printf("  Attack %d  Defense %d  Flee chance %d%%%n", hero.getAttack(), hero.getDefense(), hero.getFleeChance());
        out.printf("  Skill: %s (%d MP)%n", hero.getSkillName(), hero.getSkillCost());
        out.printf("  Gold %d  Monsters slain %d  Current score %d%n", hero.getGold(),
                game.getState().getMonstersDefeated(), game.calculateScore());
    }

    private void saveGame(GameState state) {
        try {
            gameRepository.save(state);
            out.println("  Game saved.");
        } catch (PersistenceException e) {
            out.println("  ! " + e.getMessage());
        }
    }

    private void finishRun(GameService game) {
        out.println();
        if (game.isVictory()) {
            out.println("*************** VICTORY ***************");
            out.println("  The Ember Dragon is slain and its hoard is yours!");
        } else if (!game.getState().getHero().isAlive()) {
            out.println("=============== GAME OVER ===============");
        } else {
            out.println("The adventure has come to an end.");
        }
        ScoreEntry entry = game.createScoreEntry();
        out.printf("  Final score: %d (level %d, floor %d)%n", entry.getScore(), entry.getLevel(), entry.getFloor());
        try {
            int rank = scoreService.record(entry);
            if (rank > 0) {
                out.println("  You entered the Hall of Fame at rank #" + rank + "!");
            } else {
                out.println("  Not quite enough for the Hall of Fame this time.");
            }
        } catch (PersistenceException e) {
            out.println("  ! " + e.getMessage());
        }
        try {
            gameRepository.delete();
        } catch (PersistenceException e) {
            out.println("  ! " + e.getMessage());
        }
    }

    private void showLeaderboard() {
        out.println();
        out.println("Sort the Hall of Fame by:  1) Score  2) Deepest floor  3) Name");
        Comparator<ScoreEntry> order = switch (input.readChoice("> ", 1, 3)) {
            case 1 -> ScoreService.byScore();
            case 2 -> ScoreService.byDeepestFloor();
            default -> ScoreService.byName();
        };
        try {
            List<ScoreEntry> entries = scoreService.getLeaderboard(order);
            out.println();
            out.println("================ HALL OF FAME ================");
            if (entries.isEmpty()) {
                out.println("  No legends yet. Be the first!");
                return;
            }
            out.printf("  %-3s %-16s %-8s %5s %5s %6s  %s%n", "#", "Hero", "Class", "Level", "Floor", "Score", "Result");
            for (int i = 0; i < entries.size(); i++) {
                ScoreEntry e = entries.get(i);
                out.printf("  %-3d %-16s %-8s %5d %5d %6d  %s%n", i + 1, e.getHeroName(), e.getHeroClass(),
                        e.getLevel(), e.getFloor(), e.getScore(), e.isVictory() ? "Victory" : "Fallen");
            }
        } catch (PersistenceException e) {
            out.println("  ! " + e.getMessage());
        }
    }

    private String bar(int current, int max) {
        int filled = max <= 0 ? 0 : (int) Math.round((double) current * BAR_WIDTH / max);
        return "[" + "#".repeat(filled) + "-".repeat(BAR_WIDTH - filled) + "]";
    }

    @Override
    public void onEvent(GameEvent event) {
        String prefix = switch (event.type()) {
            case COMBAT -> "  > ";
            case LOOT -> "  + ";
            case LEVEL_UP -> "  ** ";
            case WARNING -> "  ! ";
            case VICTORY -> "  *** ";
            case DEFEAT -> "  xxx ";
            default -> "  ";
        };
        out.println(prefix + event.message());
    }
}
