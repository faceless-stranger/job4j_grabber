package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private final DateTimeParser dateTimeParser;

    private static final String SOURCE_LINK = "https://career.habr.com";

    public static final int PAGE_NUMBER = 2;
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static List<Post> startParse() throws IOException {
        DateTimeParser parser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(parser);
        List<Post> result = new ArrayList();
        int page = 1;
        while (page <= PAGE_NUMBER) {
            /* Создаем ссылку из трех констант. Первая ссылка на сайт, вторая страница сайта, последняя часть наш запрос */
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, page, SUFFIX);
            result.addAll(habrCareerParse.list(fullLink));
            /* ведёт нумерацию страниц */
            page++;
        }
        return result;
    }

    /* Получает описание вакансии */
    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        return connection.get().select(".vacancy-description__text").text();
    }

    /* Создает лист Post-ов которые содержат все данные полученные при работе */
    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> list = new ArrayList<>();
        /* создает подключение к указанному URL */
        Connection connection = Jsoup.connect(link);
        /* Document: Это объект, который содержит всю структуру HTML-страницы. Который позволяет извлекать, изменять и анализировать данные из этой HTML-страницы. */
        Document document = connection.get();
        /* Далее анализируя структуру страницы мы выясняем, что признаком вакансии является CSS класс .vacancy-card__inner
         * Извлекает все элементы HTML на странице, которые имеют класс vacancy-card__inner. Простыми словами: Находит все блоки на странице с информацией о вакансиях.*/
        Elements rows = document.select(".vacancy-card__inner");
        for (Element row : rows) {
            list.add(postCreated(row));
        }
        return list;
    }

    public Post postCreated(Element row) throws IOException {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();
        Element dateElement = row.select(".vacancy-card__date").first();
        Element linkDate = dateElement.child(0);
        String date = linkDate.attr("datetime");
        String linkVacancy = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        dateTimeParser.parse(date);
        String description = retrieveDescription(linkVacancy);
        return new Post(1, vacancyName, linkVacancy, description, dateTimeParser.parse(date));
    }
}