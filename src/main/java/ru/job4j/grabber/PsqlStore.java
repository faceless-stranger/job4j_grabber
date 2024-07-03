package ru.job4j.grabber;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection connection;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /* Метод save() - сохраняет объявление в базе. */
    @Override
    public void save(Post post) {
        /* Создаем PreparedStatement и отправляем sql запрос. Statement.RETURN_GENERATED_KEYS - это константа, которая указывает JDBC-драйверу возвращать сгенерированные ключи после выполнения операции вставки данных в базу данных. */
        try (PreparedStatement pStatement =
                     connection.prepareStatement("INSERT INTO post (name, text, link, created) VALUES (?,?,?,?) "
                             + "ON CONFLICT(link) DO NOTHING", Statement.RETURN_GENERATED_KEYS)) {
            pStatement.setString(1, post.getTitle());  /* Устанавливаем title из объекта Post */
            pStatement.setString(2, post.getDescription()); /* Устанавливаем описание из объекта Post */
            pStatement.setString(3, post.getLink()); /* Устанавливаем ссылку */
            pStatement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));  /* Устанавливаем дату создания из объекта Post используя Timestamp и нужно помнить о переводе из LocalDateTime в Timestamp */
            pStatement.executeUpdate(); /* Производим вставку и записываем число */
            ResultSet generatedKeys = pStatement.getGeneratedKeys(); /*  ResultSet это интерфейс для представления результата запроса к базе данных. Он представляет собой таблицу данных, возвращенную из базы данных после выполнения SQL-запроса */
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1); /* Получаем значение столбца т.е 1 это id, если указать 2 то это будет name */
                post.setId(id); /* устанавливаем сгенерированный id */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* Метод getAll() - позволяет извлечь объявления из базы. */
    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        try (PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM post");
             ResultSet resultSet = pStatement.executeQuery()) {
            while (resultSet.next()) {
                result.add(createPost(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /* Метод findById(int id) - позволяет извлечь объявление из базы по id. */
    @Override
    public Post findById(int id) {
        Post result = null;
        try (PreparedStatement ps =
                     connection.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                result = createPost(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private Post createPost(ResultSet resultSet) {
        try {
            return new Post(resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("link"),
                    resultSet.getString("text"),
                    resultSet.getTimestamp("created").toLocalDateTime());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) throws IOException {
        /* Получим список объектов */
        List<Post> result = new ArrayList();
        result = HabrCareerParse.startParse();

        /* Подключимся к базе и начнем манипуляции с объектами Post
         *  для этого предварительно получим config*/
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(".\\src\\main\\resources\\rabbit.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Post postTest = new Post(1, "Java Developer Position", "https://example.com/job/java-developer", "We are looking for a Java Developer with 3+ years of experience.", LocalDateTime.now());
        PsqlStore psqlStore = new PsqlStore(properties);
        psqlStore.save(postTest);

        /* Загружаем базу*/
        int value = 0;
        while (value <= 30) {
            psqlStore.save(result.get(value));
            value++;
        }

        /* Выгружаем базу*/
        result = psqlStore.getAll();
        System.out.println(result);

        /* Поиск по id*/
        System.out.println(psqlStore.findById(1));
    }
}