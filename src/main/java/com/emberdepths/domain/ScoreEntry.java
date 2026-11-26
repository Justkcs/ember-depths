package com.emberdepths.domain;

import java.util.Objects;

public final class ScoreEntry implements Comparable<ScoreEntry> {

    private static final String SEPARATOR = ";";

    private final String heroName;
    private final String heroClass;
    private final int level;
    private final int floor;
    private final int score;
    private final boolean victory;

    public ScoreEntry(String heroName, String heroClass, int level, int floor, int score, boolean victory) {
        this.heroName = requireText(heroName, "Hero name");
        this.heroClass = requireText(heroClass, "Hero class");
        if (level < 1 || floor < 1) {
            throw new IllegalArgumentException("Level and floor must be at least 1");
        }
        if (score < 0) {
            throw new IllegalArgumentException("Score must not be negative");
        }
        this.level = level;
        this.floor = floor;
        this.score = score;
        this.victory = victory;
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank() || value.contains(SEPARATOR)) {
            throw new IllegalArgumentException(label + " must be non-empty and must not contain '" + SEPARATOR + "'");
        }
        return value.trim();
    }

    public static ScoreEntry fromLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line must not be null");
        }
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Expected 6 fields but found " + parts.length);
        }
        return new ScoreEntry(parts[0], parts[1], Integer.parseInt(parts[2].trim()),
                Integer.parseInt(parts[3].trim()), Integer.parseInt(parts[4].trim()),
                Boolean.parseBoolean(parts[5].trim()));
    }

    public String toLine() {
        return String.join(SEPARATOR, heroName, heroClass, String.valueOf(level),
                String.valueOf(floor), String.valueOf(score), String.valueOf(victory));
    }

    @Override
    public int compareTo(ScoreEntry other) {
        int byScore = Integer.compare(other.score, score);
        return byScore != 0 ? byScore : heroName.compareToIgnoreCase(other.heroName);
    }

    public String getHeroName() {
        return heroName;
    }

    public String getHeroClass() {
        return heroClass;
    }

    public int getLevel() {
        return level;
    }

    public int getFloor() {
        return floor;
    }

    public int getScore() {
        return score;
    }

    public boolean isVictory() {
        return victory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScoreEntry other)) {
            return false;
        }
        return level == other.level && floor == other.floor && score == other.score
                && victory == other.victory && heroName.equals(other.heroName) && heroClass.equals(other.heroClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heroName, heroClass, level, floor, score, victory);
    }
}
