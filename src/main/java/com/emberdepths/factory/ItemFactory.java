package com.emberdepths.factory;

import com.emberdepths.domain.item.HealthPotion;
import com.emberdepths.domain.item.Item;
import com.emberdepths.domain.item.ManaPotion;
import com.emberdepths.domain.item.StrengthElixir;

import java.util.Objects;
import java.util.Random;

public class ItemFactory {

    private final Random random;

    public ItemFactory(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public Item createRandom() {
        int roll = random.nextInt(100);
        if (roll < 50) {
            return new HealthPotion();
        }
        if (roll < 85) {
            return new ManaPotion();
        }
        return new StrengthElixir();
    }
}
