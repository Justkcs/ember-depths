package com.emberdepths.domain;

import com.emberdepths.domain.item.HealthPotion;
import com.emberdepths.domain.item.Inventory;
import com.emberdepths.domain.item.Item;
import com.emberdepths.domain.item.ManaPotion;
import com.emberdepths.domain.item.StrengthElixir;
import com.emberdepths.exception.InventoryFullException;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryTest {

    @Test
    void throwsWhenFull() {
        Inventory<Item> inventory = new Inventory<>(2);
        inventory.add(new HealthPotion());
        inventory.add(new ManaPotion());
        assertThrows(InventoryFullException.class, () -> inventory.add(new StrengthElixir()));
    }

    @Test
    void sortsWithComparator() {
        Inventory<Item> inventory = new Inventory<>(5);
        inventory.add(new ManaPotion());
        inventory.add(new StrengthElixir());
        inventory.add(new HealthPotion());
        inventory.sort(Comparator.comparingInt(Item::getValue).reversed());
        assertEquals("Strength Elixir", inventory.getItems().get(0).getName());
        inventory.sort(Comparator.comparing(Item::getName));
        assertEquals("Health Potion", inventory.getItems().get(0).getName());
    }

    @Test
    void rejectsInvalidIndex() {
        Inventory<Item> inventory = new Inventory<>(3);
        assertThrows(IllegalArgumentException.class, () -> inventory.removeAt(0));
    }

    @Test
    void exposesReadOnlyView() {
        Inventory<Item> inventory = new Inventory<>(3);
        inventory.add(new HealthPotion());
        assertThrows(UnsupportedOperationException.class, () -> inventory.getItems().clear());
    }
}
