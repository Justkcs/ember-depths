package com.emberdepths.service;

import com.emberdepths.domain.GameState;
import com.emberdepths.domain.ScoreEntry;
import com.emberdepths.domain.hero.Hero;
import com.emberdepths.domain.hero.SkillResult;
import com.emberdepths.domain.item.Item;
import com.emberdepths.domain.monster.AttackOutcome;
import com.emberdepths.domain.monster.Monster;
import com.emberdepths.domain.world.Dungeon;
import com.emberdepths.domain.world.Room;
import com.emberdepths.exception.InsufficientManaException;
import com.emberdepths.exception.InventoryFullException;
import com.emberdepths.service.event.EventType;
import com.emberdepths.service.event.GameEvent;
import com.emberdepths.service.event.GameEventListener;
import com.emberdepths.strategy.AttackStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameService {

    private final GameState state;
    private final Random random;
    private final List<GameEventListener> listeners = new ArrayList<>();
    private Monster currentEnemy;

    public GameService(GameState state, Random random) {
        this.state = Objects.requireNonNull(state, "state");
        this.random = Objects.requireNonNull(random, "random");
    }

    public void addListener(GameEventListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    private void publish(EventType type, String message) {
        GameEvent event = new GameEvent(type, message);
        for (GameEventListener listener : List.copyOf(listeners)) {
            listener.onEvent(event);
        }
    }

    public Room enterNextRoom() {
        if (isInCombat()) {
            throw new IllegalStateException("Finish the current fight first");
        }
        Dungeon dungeon = state.getDungeon();
        Room room = dungeon.advance();
        publish(EventType.INFO, "Floor " + room.getFloor() + ", room " + room.getNumber()
                + ": you step into " + room.getType().getDescription() + ".");
        switch (room.getType()) {
            case MONSTER, BOSS -> startCombat(room);
            case TREASURE -> collectTreasure(room);
            case SPRING -> restAtSpring();
            default -> publish(EventType.INFO, "Only dust and silence here.");
        }
        return room;
    }

    private void startCombat(Room room) {
        currentEnemy = room.getMonster().orElseThrow();
        if (currentEnemy.isBoss()) {
            publish(EventType.WARNING, "The ground trembles... the " + currentEnemy.getName() + " awakens!");
        } else {
            publish(EventType.COMBAT, "A " + currentEnemy.getName() + " blocks your path!");
        }
    }

    private void collectTreasure(Room room) {
        Hero hero = state.getHero();
        hero.addGold(room.getGold());
        publish(EventType.LOOT, "You find " + room.getGold() + " gold coins.");
        room.getTreasure().ifPresent(item -> {
            try {
                hero.getInventory().add(item);
                publish(EventType.LOOT, "You pick up a " + item.getName() + ".");
            } catch (InventoryFullException e) {
                publish(EventType.WARNING, e.getMessage() + " The " + item.getName() + " is left behind.");
            }
        });
    }

    private void restAtSpring() {
        Hero hero = state.getHero();
        int healed = hero.heal(hero.getMaxHealth() * 4 / 10);
        int restored = hero.restoreMana(hero.getMaxMana() / 2);
        publish(EventType.LOOT, "The spring restores " + healed + " HP and " + restored + " MP.");
    }

    public void attack() {
        Monster enemy = requireEnemy();
        Hero hero = state.getHero();
        int damage = enemy.receiveDamage(hero.rollAttackDamage(random));
        publish(EventType.COMBAT, hero.getName() + " hits the " + enemy.getName() + " for " + damage
                + " damage (" + hero.getStrategy().getName() + ").");
        resolveRound(enemy);
    }

    public void useSkill() throws InsufficientManaException {
        Monster enemy = requireEnemy();
        SkillResult result = state.getHero().useSkill(enemy, random);
        publish(EventType.COMBAT, result.description());
        resolveRound(enemy);
    }

    public Item useItem(int index) {
        Hero hero = state.getHero();
        Item item = hero.getInventory().removeAt(index);
        publish(EventType.LOOT, item.use(hero));
        if (isInCombat()) {
            enemyTurn(currentEnemy);
        }
        return item;
    }

    public boolean flee() {
        Monster enemy = requireEnemy();
        if (enemy.isBoss()) {
            publish(EventType.WARNING, "There is no escape from the " + enemy.getName() + "!");
            enemyTurn(enemy);
            return false;
        }
        if (random.nextInt(100) < state.getHero().getFleeChance()) {
            currentEnemy = null;
            publish(EventType.INFO, "You slip past the " + enemy.getName() + " and escape deeper into the dungeon.");
            return true;
        }
        publish(EventType.WARNING, "You fail to escape!");
        enemyTurn(enemy);
        return false;
    }

    public void changeStrategy(AttackStrategy strategy) {
        state.getHero().setStrategy(strategy);
        publish(EventType.INFO, "Combat style changed to " + strategy.getName() + " (" + strategy.getDescription() + ").");
    }

    private Monster requireEnemy() {
        if (currentEnemy == null) {
            throw new IllegalStateException("You are not in combat");
        }
        return currentEnemy;
    }

    private void resolveRound(Monster enemy) {
        if (enemy.isAlive()) {
            enemyTurn(enemy);
            return;
        }
        Hero hero = state.getHero();
        currentEnemy = null;
        state.recordMonsterDefeated(enemy);
        hero.addGold(enemy.getGoldReward());
        publish(EventType.LOOT, "The " + enemy.getName() + " is defeated! +" + enemy.getExperienceReward()
                + " XP, +" + enemy.getGoldReward() + " gold.");
        int levels = hero.gainExperience(enemy.getExperienceReward());
        if (levels > 0) {
            publish(EventType.LEVEL_UP, hero.getName() + " reached level " + hero.getLevel()
                    + "! Health and mana fully restored.");
        }
        if (enemy.isBoss()) {
            publish(EventType.VICTORY, "The " + enemy.getName() + " collapses. The Ember Depths are free!");
        }
    }

    private void enemyTurn(Monster enemy) {
        Hero hero = state.getHero();
        AttackOutcome outcome = enemy.attack(hero, random);
        publish(EventType.COMBAT, outcome.message());
        if (!hero.isAlive()) {
            publish(EventType.DEFEAT, hero.getName() + " has fallen on floor " + state.getDungeon().getCurrentFloor() + ".");
        }
    }

    public boolean isInCombat() {
        return currentEnemy != null;
    }

    public boolean isVictory() {
        return state.isBossDefeated();
    }

    public boolean isGameOver() {
        return !state.getHero().isAlive() || isVictory()
                || (!isInCombat() && !state.getDungeon().hasNextRoom());
    }

    public int calculateScore() {
        Hero hero = state.getHero();
        return hero.getGold()
                + hero.getLevel() * 100
                + state.getMonstersDefeated() * 25
                + state.getDungeon().getCurrentFloor() * 50
                + (isVictory() ? 1000 : 0);
    }

    public ScoreEntry createScoreEntry() {
        Hero hero = state.getHero();
        return new ScoreEntry(hero.getName(), hero.getTypeName(), hero.getLevel(),
                state.getDungeon().getCurrentFloor(), calculateScore(), isVictory());
    }

    public GameState getState() {
        return state;
    }

    public Monster getCurrentEnemy() {
        return currentEnemy;
    }
}
