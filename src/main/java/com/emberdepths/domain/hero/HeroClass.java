package com.emberdepths.domain.hero;

public enum HeroClass {

    WARRIOR("Warrior", "Sturdy fighter with high health and defense. Skill: Whirlwind Strike"),
    MAGE("Mage", "Fragile caster whose Fireball ignores armor. Skill: Fireball"),
    ROGUE("Rogue", "Agile trickster with critical strikes and easy escapes. Skill: Backstab");

    private final String displayName;
    private final String description;

    HeroClass(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
