package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        // Преобразуем строку в OffsetDateTime, который учитывает временную зону
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(parse);
        // Преобразуем OffsetDateTime в LocalDateTime, который не учитывает временную зону
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        // Выводим результат для проверки
        return localDateTime;
    }
}