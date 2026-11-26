package com.emberdepths.service;

import com.emberdepths.domain.ScoreEntry;
import com.emberdepths.exception.PersistenceException;
import com.emberdepths.persistence.ScoreRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ScoreService {

    public static final int MAX_ENTRIES = 10;

    private final ScoreRepository repository;

    public ScoreService(ScoreRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public static Comparator<ScoreEntry> byScore() {
        return Comparator.naturalOrder();
    }

    public static Comparator<ScoreEntry> byDeepestFloor() {
        return Comparator.comparingInt(ScoreEntry::getFloor).reversed().thenComparing(Comparator.naturalOrder());
    }

    public static Comparator<ScoreEntry> byName() {
        return Comparator.comparing(ScoreEntry::getHeroName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Comparator.naturalOrder());
    }

    public List<ScoreEntry> getLeaderboard(Comparator<ScoreEntry> order) throws PersistenceException {
        List<ScoreEntry> entries = new ArrayList<>(repository.loadAll());
        entries.sort(Objects.requireNonNull(order, "order"));
        return Collections.unmodifiableList(entries);
    }

    public int record(ScoreEntry entry) throws PersistenceException {
        Objects.requireNonNull(entry, "entry");
        List<ScoreEntry> entries = new ArrayList<>(repository.loadAll());
        entries.add(entry);
        Collections.sort(entries);
        List<ScoreEntry> top = new ArrayList<>(entries.subList(0, Math.min(MAX_ENTRIES, entries.size())));
        repository.saveAll(top);
        int rank = top.indexOf(entry);
        return rank < 0 ? -1 : rank + 1;
    }
}
