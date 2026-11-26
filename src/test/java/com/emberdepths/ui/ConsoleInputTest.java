package com.emberdepths.ui;

import com.emberdepths.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsoleInputTest {

    @Test
    void parsesValidChoice() throws InvalidInputException {
        assertEquals(3, ConsoleInput.parseChoice(" 3 ", 1, 5));
    }

    @Test
    void rejectsTextAndOutOfRangeNumbers() {
        assertThrows(InvalidInputException.class, () -> ConsoleInput.parseChoice("abc", 1, 5));
        assertThrows(InvalidInputException.class, () -> ConsoleInput.parseChoice("9", 1, 5));
        assertThrows(InvalidInputException.class, () -> ConsoleInput.parseChoice("", 1, 5));
    }
}
