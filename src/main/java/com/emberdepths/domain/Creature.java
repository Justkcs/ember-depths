package com.emberdepths.domain;

import java.io.Serializable;

public abstract class Creature implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private int maxHealth;
    private int health;
    private int attack;
    private int defense;

    protected Creature(String name, int maxHealth, int attack, int defense) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        if (attack < 0 || defense < 0) {
            throw new IllegalArgumentException("Attack and defense must not be negative");
        }
        this.name = name.trim();
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.defense = defense;
    }

    public int receiveDamage(int rawDamage) {
        int damage = Math.max(1, rawDamage - getEffectiveDefense() / 2);
        return applyDamage(damage);
    }

    public int receiveTrueDamage(int rawDamage) {
        return applyDamage(Math.max(0, rawDamage));
    }

    private int applyDamage(int damage) {
        health = Math.max(0, health - damage);
        return damage;
    }

    public int heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount must not be negative");
        }
        int before = health;
        health = Math.min(maxHealth, health + amount);
        return health - before;
    }

    public boolean isAlive() {
        return health > 0;
    }

    protected int getEffectiveDefense() {
        return defense;
    }

    protected void increaseMaxHealth(int amount) {
        maxHealth += amount;
        health += amount;
    }

    protected void increaseAttack(int amount) {
        attack += amount;
    }

    protected void increaseDefense(int amount) {
        defense += amount;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public abstract String getTypeName();
}
