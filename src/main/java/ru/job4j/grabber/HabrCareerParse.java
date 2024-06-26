package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final int PAGE_NUMBER = 5;
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    public static void main(String[] args) throws IOException {
        int pageNumber = 1;
        while (pageNumber <= 5) {
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
                HabrCareerDateTimeParser timeParser = new HabrCareerDateTimeParser();
                LocalDateTime localDateTime = timeParser.parse(date);
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("Должность - %s%nДата - %s%nСсылка - %s%n%n--------------------------------%n", vacancyName, localDateTime, link);
            });
            System.out.println(String.format("%n-----------------Страница %d -----------------%n", pageNumber));
            pageNumber++;
        }
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        return connection.get().select(".vacancy-description__text").text();
    }
}