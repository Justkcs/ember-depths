package com.emberdepths.gui;

import com.emberdepths.domain.GameState;
import com.emberdepths.domain.hero.Hero;
import com.emberdepths.exception.PersistenceException;
import com.emberdepths.factory.GameFactory;
import com.emberdepths.persistence.GameRepository;
import com.emberdepths.service.ScoreService;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.Objects;
import java.util.Random;

public class GameWindow extends JFrame {

    private static final String MENU = "menu";
    private static final String CREATE = "create";
    private static final String GAME = "game";
    private static final String SCORES = "scores";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final GameRepository gameRepository;
    private final GameFactory gameFactory;
    private final MenuPanel menuPanel;
    private final CreateHeroPanel createPanel;
    private final GamePanel gamePanel;
    private final LeaderboardPanel leaderboardPanel;

    public GameWindow(GameRepository gameRepository, ScoreService scoreService, Random random) {
        super("Ember Depths");
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository");
        Objects.requireNonNull(scoreService, "scoreService");
        this.gameFactory = new GameFactory(random);
        this.menuPanel = new MenuPanel(this);
        this.createPanel = new CreateHeroPanel(this);
        this.gamePanel = new GamePanel(this, gameRepository, scoreService, random);
        this.leaderboardPanel = new LeaderboardPanel(this, scoreService);
        root.add(menuPanel, MENU);
        root.add(createPanel, CREATE);
        root.add(gamePanel, GAME);
        root.add(leaderboardPanel, SCORES);
        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 740));
        setSize(1200, 820);
        setLocationRelativeTo(null);
        showMenu();
    }

    public void showMenu() {
        menuPanel.refresh(gameRepository.hasSave());
        cards.show(root, MENU);
    }

    public void showCreateHero() {
        menuPanel.stop();
        createPanel.reset();
        cards.show(root, CREATE);
    }

    public void showLeaderboard() {
        menuPanel.stop();
        leaderboardPanel.reload();
        cards.show(root, SCORES);
    }

    public void beginAdventure(Hero hero) {
        if (gameRepository.hasSave()) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Starting a new adventure will overwrite your saved game. Continue?",
                    "Ember Depths", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }
        showGame(gameFactory.createNewGame(hero));
    }

    public void continueAdventure() {
        try {
            showGame(gameRepository.load());
        } catch (PersistenceException e) {
            showError(e.getMessage());
        }
    }

    private void showGame(GameState state) {
        menuPanel.stop();
        gamePanel.start(state);
        cards.show(root, GAME);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ember Depths", JOptionPane.ERROR_MESSAGE);
    }
}
