package com.emberdepths.persistence;

import com.emberdepths.domain.ScoreEntry;
import com.emberdepths.exception.PersistenceException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextScoreRepository implements ScoreRepository {

    private final Path file;
    private int skippedLines;

    public TextScoreRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    public List<ScoreEntry> loadAll() throws PersistenceException {
        skippedLines = 0;
        List<ScoreEntry> entries = new ArrayList<>();
        if (!Files.exists(file)) {
            return entries;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    entries.add(ScoreEntry.fromLine(line));
                } catch (IllegalArgumentException e) {
                    skippedLines++;
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Could not read the Hall of Fame from " + file, e);
        }
        return entries;
    }

    @Override
    public void saveAll(List<ScoreEntry> entries) throws PersistenceException {
        Objects.requireNonNull(entries, "entries");
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                for (ScoreEntry entry : entries) {
                    writer.write(entry.toLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Could not write the Hall of Fame to " + file, e);
        }
    }

    public int getSkippedLines() {
        return skippedLines;
    }
}
