package com.emberdepths.domain.item;

import com.emberdepths.domain.hero.Hero;

import java.io.Serializable;

public abstract class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final int value;

    protected Item(String name, int value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be empty");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Item value must not be negative");
        }
        this.name = name;
        this.value = value;
    }

    public abstract String use(Hero hero);

    public abstract String getDescription();

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + " (" + getDescription() + ")";
    }
}
