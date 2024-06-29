package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HaberCareerDateTimeParserTest {

    @Test
    void testParseValidDateTimeString() {
        String dateTimeString = "2024-06-29T15:30:00+03:00";
        LocalDateTime expected = LocalDateTime.of(2024, 6, 29, 15, 30, 0);

        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(dateTimeString);

        assertEquals(expected, result);
    }

    @Test
    void testParseDifferentDateTimeString() {
        String dateTimeString = "2023-12-25T10:45:30+01:00";
        LocalDateTime expected = LocalDateTime.of(2023, 12, 25, 10, 45, 30);

        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(dateTimeString);

        assertEquals(expected, result);
    }

    @Test
    void testParseDateTimeWithZeroOffset() {
        String dateTimeString = "2022-01-01T12:00:00Z";
        LocalDateTime expected = LocalDateTime.of(2022, 1, 1, 12, 0, 0);

        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(dateTimeString);

        assertEquals(expected, result);
    }
}