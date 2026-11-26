package com.emberdepths.domain.hero;

import com.emberdepths.domain.Creature;
import com.emberdepths.domain.item.Inventory;
import com.emberdepths.domain.item.Item;
import com.emberdepths.exception.InsufficientManaException;
import com.emberdepths.strategy.AttackStrategy;
import com.emberdepths.strategy.BalancedStrategy;

import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

public abstract class Hero extends Creature {

    private static final long serialVersionUID = 1L;
    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z0-9 ]{2,16}");
    public static final int INVENTORY_CAPACITY = 8;

    private int level;
    private int experience;
    private int gold;
    private int maxMana;
    private int mana;
    private final Inventory<Item> inventory;
    private AttackStrategy strategy;

    protected Hero(String name, int maxHealth, int attack, int defense, int maxMana) {
        super(validateName(name), maxHealth, attack, defense);
        if (maxMana < 0) {
            throw new IllegalArgumentException("Mana must not be negative");
        }
        this.level = 1;
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.inventory = new Inventory<>(INVENTORY_CAPACITY);
        this.strategy = new BalancedStrategy();
    }

    private static String validateName(String name) {
        if (name == null || !NAME_PATTERN.matcher(name.trim()).matches()) {
            throw new IllegalArgumentException("Hero name must be 2-16 letters, digits or spaces");
        }
        return name.trim();
    }

    public int rollAttackDamage(Random random) {
        return strategy.calculateDamage(getAttack(), random);
    }

    @Override
    protected int getEffectiveDefense() {
        return strategy.adjustDefense(getDefense());
    }

    public final SkillResult useSkill(Creature target, Random random) throws InsufficientManaException {
        Objects.requireNonNull(target, "target");
        if (!target.isAlive()) {
            throw new IllegalStateException("Target is already defeated");
        }
        if (mana < getSkillCost()) {
            throw new InsufficientManaException(getSkillName(), getSkillCost(), mana);
        }
        mana -= getSkillCost();
        return performSkill(target, random);
    }

    protected abstract SkillResult performSkill(Creature target, Random random);

    public abstract String getSkillName();

    public abstract int getSkillCost();

    public abstract HeroClass getHeroClass();

    protected abstract void applyLevelUpBonus();

    public int getFleeChance() {
        return 50;
    }

    public int gainExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience must not be negative");
        }
        experience += amount;
        int levelsGained = 0;
        while (experience >= getExperienceToNextLevel()) {
            experience -= getExperienceToNextLevel();
            level++;
            levelsGained++;
            applyLevelUpBonus();
            heal(getMaxHealth());
            mana = maxMana;
        }
        return levelsGained;
    }

    public int getExperienceToNextLevel() {
        return level * 50;
    }

    public void addGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold must not be negative");
        }
        gold += amount;
    }

    public int restoreMana(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Mana amount must not be negative");
        }
        int before = mana;
        mana = Math.min(maxMana, mana + amount);
        return mana - before;
    }

    public void boostAttack(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Attack boost must be positive");
        }
        increaseAttack(amount);
    }

    protected void increaseMaxMana(int amount) {
        maxMana += amount;
        mana += amount;
    }

    public void setStrategy(AttackStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    @Override
    public final String getTypeName() {
        return getHeroClass().getDisplayName();
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getGold() {
        return gold;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public Inventory<Item> getInventory() {
        return inventory;
    }

    public AttackStrategy getStrategy() {
        return strategy;
    }
}
