package quartz.quartzscheduler1;

import helper.PrintUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        PrintUtils.printString("Hello World! - " + new Date());
    }
}
