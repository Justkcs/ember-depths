package com.emberdepths.ui;

import com.emberdepths.exception.InputClosedException;
import com.emberdepths.exception.InvalidInputException;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleInput {

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleInput(InputStream in, PrintStream out) {
        this.scanner = new Scanner(Objects.requireNonNull(in, "in"), StandardCharsets.UTF_8);
        this.out = Objects.requireNonNull(out, "out");
    }

    public String readLine(String prompt) {
        out.print(prompt);
        out.flush();
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine().replace("﻿", "").trim();
    }

    public int readChoice(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                return parseChoice(line, min, max);
            } catch (InvalidInputException e) {
                out.println("  ! " + e.getMessage());
            }
        }
    }

    public static int parseChoice(String text, int min, int max) throws InvalidInputException {
        if (text == null || text.isBlank()) {
            throw new InvalidInputException("Please type a number between " + min + " and " + max + ".");
        }
        int value;
        try {
            value = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("'" + text.trim() + "' is not a number.");
        }
        if (value < min || value > max) {
            throw new InvalidInputException("Please type a number between " + min + " and " + max + ".");
        }
        return value;
    }
}
