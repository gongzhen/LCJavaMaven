package quartz.quartzscheduler1;

import helper.PrintUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzSchedulerMain1 {

    private static void test1() {
        SchedulerFactory sf = new StdSchedulerFactory();
        try {
            Scheduler sched = sf.getScheduler();
            JobDetail job = JobBuilder.newJob(HelloJob.class)
                    .withIdentity("HelloJob")
                    .build();
            JobKey jobKey = job.getKey();
            PrintUtils.printString("job key: " + jobKey);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(30)
                                    .repeatForever())
                    .build();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("crontrigger","crontriggergroup1")
                    .withSchedule(CronScheduleBuilder.cronSchedule("10 * * * * ?"))
                    .build();
            sched.start();
            sched.scheduleJob(job, trigger);
            sched.pauseJob(job.getKey());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test1();
    }
}
