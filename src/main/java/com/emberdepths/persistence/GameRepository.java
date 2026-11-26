package com.emberdepths.persistence;

import com.emberdepths.domain.GameState;
import com.emberdepths.exception.PersistenceException;

public interface GameRepository {

    void save(GameState state) throws PersistenceException;

    GameState load() throws PersistenceException;

    boolean hasSave();

    void delete() throws PersistenceException;
}
