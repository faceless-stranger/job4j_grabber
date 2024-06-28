package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    public static void main(String[] args) throws IOException {
        int pageNumber = 1;
        /* Создаем ссылку из трех констант. Первая ссылка на сайт, вторая страница сайта, последняя часть наш запрос */
        String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
        /* создает подключение к указанному URL */
        Connection connection = Jsoup.connect(fullLink);
        /* Document: Это объект, который содержит всю структуру HTML-страницы. Который позволяет извлекать, изменять и анализировать данные из этой HTML-страницы. */
        Document document = connection.get();
        /* Далее анализируя структуру страницы мы выясняем, что признаком вакансии является CSS класс .vacancy-card__inner
         * Извлекает все элементы HTML на странице, которые имеют класс vacancy-card__inner. Простыми словами: Находит все блоки на странице с информацией о вакансиях.*/
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first(); /* Находит первый элемент внутри текущего блока вакансии, который имеет класс vacancy-card__title. */
            Element linkElement = titleElement.child(0);
            String vacancyName = titleElement.text();

            Element dateElement = row.select(".vacancy-card__date").first();
            Element linkDate = dateElement.child(0);
            String date = linkDate.attr("datetime");

            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            System.out.printf("Должность - %s%nДата - %s%nСсылка - %s%n%n--------------------------------%n", vacancyName, date, link);
        });
    }
}