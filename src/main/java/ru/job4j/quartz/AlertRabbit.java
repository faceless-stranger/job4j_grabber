package ru.job4j.quartz;

import org.quartz.*; /* Это библиотека на языке программирования Java, предназначенная для планирования и управления выполнением задач. Она позволяет разработчикам определять, когда и как часто должны выполняться определенные задачи или процессы. */
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) throws Exception {
        try {
            Properties properties = loadProperty();
            int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
            /* Создаем подключение к базе исходя из логи что бы не перегружать программу в методе execute
            *  постоянными соединениями */
            Class.forName(properties.getProperty("driver-class-name"));
            Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));
            /* JobDataMap — это класс из библиотеки Quartz работа которого похожа на HashMap.
             * Он предназначен для передачи данных между компонентами Quartz и благодаря ему мы передадим соединение в метод execute  */
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000); /* Этот метод не относиться к Quartz, и позволяет приостановить программу, но пользоваться этим нужно аккуратно */
            scheduler.shutdown();
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static Properties loadProperty() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(".\\src\\main\\resources\\rabbit.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

public static class Rabbit implements Job { /* Quartz каждый раз создает объект с типом org.quartz. Job. Нам нужно создать класс реализующий этот интерфейс. Внутри этого класса нужно описать требуемые действия. */
        @Override
        public void execute(JobExecutionContext context) {
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            LocalDateTime currentTime = LocalDateTime.now();
            try (PreparedStatement pSatement = connection.prepareStatement("INSERT INTO rabbit (created_date) VALUES (?)");) {
                pSatement.setTimestamp(1, Timestamp.valueOf(currentTime));
                pSatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Планировщик работает");

        }
    }
}