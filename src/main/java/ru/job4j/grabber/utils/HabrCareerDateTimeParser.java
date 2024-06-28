package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        String trimmedDateTime = parse.substring(0, parse.indexOf('+'));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(trimmedDateTime, formatter);
        System.out.println(localDateTime);
        return localDateTime;
    }

    public static void main(String[] args) {
        String test = "2024-06-27T12:16:02+03:00";
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        habrCareerDateTimeParser.parse(test);
    }

}