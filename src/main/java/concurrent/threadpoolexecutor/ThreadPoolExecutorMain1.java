package concurrent.threadpoolexecutor;

import helper.PrintUtils;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorMain1 {

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> future;

    public ThreadPoolExecutorMain1() {
        scheduledExecutorService = Executors.newScheduledThreadPool(4);
    }

    Runnable task() {
        return () -> {
            Thread currentThread = Thread.currentThread();
            PrintUtils.printString("id of the thread is " + currentThread.getId());
        };
    }

    public void testScheduledExecutor() {
        while(true){
            Scanner input = new Scanner(System.in);
            int enabled = input.nextInt();
            if(enabled % 2 == 0) {
                enableTask();
            } else if (enabled % 2 == 1) {
                disableTask();
            } else
                System.out.println(enabled + " not from 0 or 1.");
        }
    }

    public void disableTask() {
        PrintUtils.printString("disableTask");
        scheduledExecutorService.shutdown();
    }

    public void enableTask() {
        PrintUtils.printString("enableTask");
        future = scheduledExecutorService.scheduleAtFixedRate(task(), 2000, 2000, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutorMain1 obj = new ThreadPoolExecutorMain1();

        obj.testScheduledExecutor();
    }
}
