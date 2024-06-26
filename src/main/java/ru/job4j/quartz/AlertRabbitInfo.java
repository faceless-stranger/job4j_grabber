package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbitInfo {

    public static void main(String[] args) throws Exception {
        try {
        /* StdSchedulerFactory: Это класс из библиотеки Quartz, который создает экземпляров Scheduler, а метод getDefaultScheduler() возвращает экземпляр планировщика (scheduler) по умолчанию.
        *  а scheduler.start()  запускает планировщик задач. После запуска планировщик начинает выполнение запланированных задач в соответствии с заданными расписаниями.
        *  Но в нашем случае он выдаст только логи, распишем задачи далее*/
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();

        /* А вот здесь мы уже можем приступить к описанию задачи в планировщике Quartz. newJob(AlertRabbit.Rabbit.class) - создает новую задачу (job)
        с указанным классом AlertRabbit.Rabbit. В вашем случае, класс AlertRabbit.Rabbit должен реализовывать интерфейс org.quartz.Job, который представляет
        метод execute(), содержащий логику выполнения задачи. Но в данный момент результат метода execute мы не получим т.к. нет триггера для запуска
        .build() - завершает создание задачи (job) и возвращает объект JobBuilder, который используется для настройки атрибутов задачи.*/
        JobDetail job = newJob(AlertRabbitInfo.PlanJob.class)
                .build();

        /* Но перед тем как мы создадим триггер, установим расписание его работы в .withIntervalInSeconds() указываем интервал
        * а в .repeatForever() обозначаем что программа работает без остановки */
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(10)
                .repeatForever();

        /* Теперь время установить триггер для запуска, в нашем случае указываем приступить к
        * работе сейчас. После устанавливаем наше расписание в withSchedule() и строим проект.*/
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();

        /* Но на данном этапе наш проект не будет выполнять логику execute. Хоть планировщик уже и запущен (scheduler.start()
         * и триггер есть надо их связать, и зарегистрировать в планировщике чтобы они начали работать согласно установленному расписанию.
         * Для этого нам нужен метод .scheduleJob */
        scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class PlanJob implements Job { /* Quartz каждый раз создает объект с типом org.quartz.Job. Нам нужно создать класс реализующий этот интерфейс. Внутри этого класса нужно описать требуемые действия. */
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println(" Планировщик работает");
        }
    }
}
