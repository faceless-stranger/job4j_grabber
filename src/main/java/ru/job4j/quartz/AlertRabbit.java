package ru.job4j.quartz;

import org.quartz.*; /* Это библиотека на языке программирования Java, предназначенная для планирования и управления выполнением задач. Она позволяет разработчикам определять, когда и как часто должны выполняться определенные задачи или процессы. */
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Properties properties = loadProperty();
            int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
            System.out.println(interval);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler(); /* StdSchedulerFactory: Это класс из библиотеки Quartz, который создает экземпляров Scheduler, а метод getDefaultScheduler() возвращает экземпляр планировщика (scheduler) по умолчанию.*/
            scheduler.start();  /*Этот вызов метода запускает планировщик, что позволяет ему начать выполнять запланированные задачи. */
            JobDetail job = newJob(Rabbit.class).build(); /* Создание задачи */
            SimpleScheduleBuilder times = simpleSchedule() /* Создание расписания. В нашем случае, мы будем запускать задачу через 10 секунд и делать это бесконечно. */
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()  /* Создаёт новый объект триггера. Триггер — это компонент, который указывает планировщику, когда запускать задачу. */
                    .startNow()             /* Говорит триггеру начать выполнение задачи немедленно. */
                    .withSchedule(times)    /* Повторять задачу бесконечно */
                    .build();
            scheduler.scheduleJob(job, trigger); /* Загрузка задачи и триггера в планировщик */
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

    public static class Rabbit implements Job { /* quartz каждый раз создает объект с типом org.quartz.Job. Нам нужно создать класс реализующий этот интерфейс. Внутри этого класса нужно описать требуемые действия. */
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
        }
    }
}