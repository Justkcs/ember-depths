package com.emberdepths.persistence;

import com.emberdepths.domain.ScoreEntry;
import com.emberdepths.exception.PersistenceException;

import java.util.List;

public interface ScoreRepository {

    List<ScoreEntry> loadAll() throws PersistenceException;

    void saveAll(List<ScoreEntry> entries) throws PersistenceException;
}
