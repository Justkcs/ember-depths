package com.emberdepths.gui;

import com.emberdepths.domain.GameState;
import com.emberdepths.domain.ScoreEntry;
import com.emberdepths.domain.hero.Hero;
import com.emberdepths.domain.item.Inventory;
import com.emberdepths.domain.item.Item;
import com.emberdepths.domain.monster.Monster;
import com.emberdepths.exception.InsufficientManaException;
import com.emberdepths.exception.PersistenceException;
import com.emberdepths.persistence.GameRepository;
import com.emberdepths.service.GameService;
import com.emberdepths.service.ScoreService;
import com.emberdepths.service.event.EventType;
import com.emberdepths.service.event.GameEvent;
import com.emberdepths.service.event.GameEventListener;
import com.emberdepths.strategy.AggressiveStrategy;
import com.emberdepths.strategy.AttackStrategy;
import com.emberdepths.strategy.BalancedStrategy;
import com.emberdepths.strategy.DefensiveStrategy;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GamePanel extends JPanel implements GameEventListener {

    private static final int MAX_SHORTCUTS = 6;

    private final GameWindow window;
    private final GameRepository gameRepository;
    private final ScoreService scoreService;
    private final Random random;
    private final ScenePanel scene = new ScenePanel();
    private final JTextPane log = new JTextPane();
    private final JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
    private final List<AttackStrategy> strategies =
            List.of(new BalancedStrategy(), new AggressiveStrategy(), new DefensiveStrategy());
    private final JComboBox<AttackStrategy> styleBox = new JComboBox<>(strategies.toArray(new AttackStrategy[0]));
    private final List<JButton> currentButtons = new ArrayList<>();
    private GameService game;
    private boolean updatingStyle;
    private boolean finished;

    public GamePanel(GameWindow window, GameRepository gameRepository, ScoreService scoreService, Random random) {
        super(new BorderLayout());
        this.window = Objects.requireNonNull(window, "window");
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository");
        this.scoreService = Objects.requireNonNull(scoreService, "scoreService");
        this.random = Objects.requireNonNull(random, "random");
        setBackground(Theme.BACKGROUND);
        add(scene, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        configureStyleBox();
        registerShortcuts();
    }

    private JPanel createBottomPanel() {
        log.setEditable(false);
        log.setFocusable(false);
        log.setBackground(Theme.LOG_BACKGROUND);
        log.setFont(new Font("Consolas", Font.PLAIN, 15));
        log.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(1100, 150));
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.BORDER));
        actions.setBackground(Theme.PANEL);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(scroll, BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.SOUTH);
        return bottom;
    }

    private void configureStyleBox() {
        styleBox.setFocusable(false);
        styleBox.setFont(Theme.font(Font.PLAIN, 14));
        styleBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AttackStrategy strategy) {
                    setText(index < 0 ? strategy.getName() : strategy.getName() + "  (" + strategy.getDescription() + ")");
                }
                return this;
            }
        });
        styleBox.addActionListener(e -> {
            if (!updatingStyle && game != null && !game.isGameOver()) {
                AttackStrategy selected = (AttackStrategy) styleBox.getSelectedItem();
                if (selected != null && selected != game.getState().getHero().getStrategy()) {
                    perform(() -> game.changeStrategy(selected));
                }
            }
        });
    }

    private void registerShortcuts() {
        for (int i = 0; i < MAX_SHORTCUTS; i++) {
            int index = i;
            String key = "shortcut" + i;
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Character.forDigit(i + 1, 10)), key);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD" + (i + 1)), key);
            getActionMap().put(key, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isShowing() && index < currentButtons.size() && currentButtons.get(index).isEnabled()) {
                        currentButtons.get(index).doClick();
                    }
                }
            });
        }
    }

    public void start(GameState state) {
        game = new GameService(state, random);
        game.addListener(this);
        finished = false;
        log.setText("");
        scene.setGame(game);
        Hero hero = state.getHero();
        append(EventType.INFO, hero.getName() + " the " + hero.getTypeName() + " lights a torch and enters the Ember Depths...");
        append(EventType.INFO, "Tip: press the number keys 1-6 to use the buttons.");
        refresh();
    }

    private void refresh() {
        actions.removeAll();
        currentButtons.clear();
        if (game.isGameOver()) {
            addAction("Continue", this::finishRun);
        } else if (game.isInCombat()) {
            Hero hero = game.getState().getHero();
            addAction("Attack", () -> perform(game::attack));
            addAction(hero.getSkillName() + " (" + hero.getSkillCost() + " MP)", () -> perform(game::useSkill));
            addInventoryAction();
            addAction("Flee", () -> perform(game::flee));
            addStyleBox();
        } else {
            addAction("Go deeper", () -> perform(game::enterNextRoom));
            addInventoryAction();
            addAction("Save", this::save);
            addAction("Save & menu", () -> {
                save();
                scene.stop();
                window.showMenu();
            });
            addStyleBox();
        }
        actions.revalidate();
        actions.repaint();
        syncStyleBox();
    }

    private void addAction(String label, Runnable action) {
        JButton button = Theme.button((currentButtons.size() + 1) + "  " + label);
        button.addActionListener(e -> action.run());
        currentButtons.add(button);
        actions.add(button);
    }

    private void addInventoryAction() {
        Inventory<Item> inventory = game.getState().getHero().getInventory();
        addAction("Items (" + inventory.size() + "/" + inventory.getCapacity() + ")", () -> {
            JButton anchor = currentButtons.stream()
                    .filter(b -> b.getText().contains("Items"))
                    .findFirst()
                    .orElse(currentButtons.get(0));
            showInventory(anchor);
        });
    }

    private void addStyleBox() {
        JLabel label = new JLabel("Style:");
        label.setForeground(Theme.MUTED);
        label.setFont(Theme.font(Font.PLAIN, 14));
        actions.add(label);
        actions.add(styleBox);
    }

    private void syncStyleBox() {
        updatingStyle = true;
        String current = game.getState().getHero().getStrategy().getName();
        strategies.stream().filter(s -> s.getName().equals(current)).findFirst().ifPresent(styleBox::setSelectedItem);
        updatingStyle = false;
    }

    private void showInventory(Component anchor) {
        Inventory<Item> inventory = game.getState().getHero().getInventory();
        JPopupMenu menu = new JPopupMenu();
        if (inventory.isEmpty()) {
            JMenuItem empty = new JMenuItem("Your bag is empty");
            empty.setEnabled(false);
            menu.add(empty);
        }
        List<Item> items = inventory.getItems();
        for (int i = 0; i < items.size(); i++) {
            int index = i;
            Item item = items.get(i);
            JMenuItem entry = new JMenuItem(item.getName() + "   -   " + item.getDescription() + "   (" + item.getValue() + " gold)");
            entry.setFont(Theme.font(Font.PLAIN, 14));
            entry.addActionListener(e -> perform(() -> game.useItem(index)));
            menu.add(entry);
        }
        if (inventory.size() > 1) {
            menu.addSeparator();
            JMenuItem byName = new JMenuItem("Sort by name");
            byName.addActionListener(e -> inventory.sort(Comparator.comparing(Item::getName)));
            JMenuItem byValue = new JMenuItem("Sort by value");
            byValue.addActionListener(e -> inventory.sort(Comparator.comparingInt(Item::getValue).reversed()));
            menu.add(byName);
            menu.add(byValue);
        }
        menu.show(anchor, 0, -menu.getPreferredSize().height);
    }

    private void perform(GameAction action) {
        Hero hero = game.getState().getHero();
        Monster enemy = game.getCurrentEnemy();
        int heroBefore = hero.getHealth();
        int enemyBefore = enemy == null ? 0 : enemy.getHealth();
        try {
            action.run();
        } catch (InsufficientManaException e) {
            append(EventType.WARNING, e.getMessage());
            scene.heroMessage("No mana!", Theme.MANA);
        } catch (IllegalStateException | IllegalArgumentException e) {
            append(EventType.WARNING, e.getMessage());
        }
        if (enemy != null) {
            int dealt = enemyBefore - enemy.getHealth();
            if (dealt > 0) {
                scene.enemyHit(dealt);
            }
            if (!enemy.isAlive()) {
                scene.enemyDefeated(enemy);
            }
        }
        int change = hero.getHealth() - heroBefore;
        if (change < 0) {
            scene.heroHit(-change, enemy != null ? 350 : 0);
        } else if (change > 0) {
            scene.heroHealed(change);
        }
        refresh();
    }

    private void save() {
        try {
            gameRepository.save(game.getState());
            append(EventType.INFO, "Game saved.");
        } catch (PersistenceException e) {
            window.showError(e.getMessage());
        }
    }

    private void finishRun() {
        if (finished) {
            return;
        }
        finished = true;
        ScoreEntry entry = game.createScoreEntry();
        String result;
        try {
            int rank = scoreService.record(entry);
            result = rank > 0
                    ? "You entered the Hall of Fame at rank #" + rank + "!"
                    : "Not quite enough for the Hall of Fame this time.";
        } catch (PersistenceException e) {
            result = e.getMessage();
        }
        try {
            gameRepository.delete();
        } catch (PersistenceException e) {
            result = result + "\n" + e.getMessage();
        }
        String title = game.isVictory() ? "Victory!" : "Game over";
        JOptionPane.showMessageDialog(this,
                "Final score: " + entry.getScore() + "  (level " + entry.getLevel() + ", floor " + entry.getFloor() + ")\n" + result,
                title, JOptionPane.INFORMATION_MESSAGE);
        scene.stop();
        window.showLeaderboard();
    }

    @Override
    public void onEvent(GameEvent event) {
        append(event.type(), event.message());
    }

    private void append(EventType type, String message) {
        StyledDocument document = log.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, colorFor(type));
        StyleConstants.setBold(style, type == EventType.LEVEL_UP || type == EventType.VICTORY || type == EventType.DEFEAT);
        try {
            document.insertString(document.getLength(), message + "\n", style);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
        log.setCaretPosition(document.getLength());
    }

    private Color colorFor(EventType type) {
        return switch (type) {
            case COMBAT -> new Color(0xF2B39A);
            case LOOT -> Theme.EXPERIENCE;
            case LEVEL_UP -> Theme.HEAL;
            case WARNING -> new Color(0xFF8A65);
            case VICTORY -> new Color(0xFFD35C);
            case DEFEAT -> Theme.HEALTH;
            default -> Theme.TEXT;
        };
    }

    @FunctionalInterface
    private interface GameAction {
        void run() throws InsufficientManaException;
    }
}
