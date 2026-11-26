package com.emberdepths.persistence;

import com.emberdepths.domain.GameState;
import com.emberdepths.exception.PersistenceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class SerializedGameRepository implements GameRepository {

    private final Path file;

    public SerializedGameRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    public void save(GameState state) throws PersistenceException {
        Objects.requireNonNull(state, "state");
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(state);
            }
        } catch (IOException e) {
            throw new PersistenceException("Could not save the game to " + file, e);
        }
    }

    @Override
    public GameState load() throws PersistenceException {
        if (!hasSave()) {
            throw new PersistenceException("No saved game was found.");
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            Object data = in.readObject();
            if (data instanceof GameState state) {
                return state;
            }
            throw new PersistenceException("The save file does not contain a game.");
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException("The save file is corrupted or from an incompatible version.", e);
        }
    }

    @Override
    public boolean hasSave() {
        return Files.isRegularFile(file);
    }

    @Override
    public void delete() throws PersistenceException {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new PersistenceException("Could not delete the save file " + file, e);
        }
    }
}
