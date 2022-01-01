package concurrent.atomic;

import helper.PrintUtils;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceMain1 {

    AtomicReference<ScheduledFuture> futureRef = new AtomicReference<>();
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    Runnable task() {
        return () -> {
            Thread currentThread = Thread.currentThread();
            PrintUtils.printString("id of the thread is " + currentThread.getId());
        };
    }

    public void disableTask() {
        futureRef.getAndUpdate(taskFuture -> {
            PrintUtils.printString("cancel is true");
            taskFuture.cancel(true);
            return taskFuture;
        });
    }

    public void enableTask() {
        PrintUtils.printString("enale is true");

        futureRef.getAndUpdate(test -> {
            PrintUtils.printString("getAndUpdate:" + test);
            if (test != null && !test.isCancelled()) {
                PrintUtils.printString("test is cancel now");
                test.cancel(true);
            }
            PrintUtils.printString("server is scheduled now");
            test = service.scheduleAtFixedRate(task(), 2, 2, TimeUnit.SECONDS);
            return test;
        });

//        futureRef.updateAndGet(test -> {
//            PrintUtils.printString("updateAndGet:" + test);
//            if (test != null && !test.isCancelled()) {
//                PrintUtils.printString("test is cancel now");
//                test.cancel(true);
//            }
//            PrintUtils.printString("server is scheduled now");
//            service.scheduleAtFixedRate(task(), 2, 2, TimeUnit.SECONDS);
//            return test;
//        });
    }

    public void test() {
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

    public static void main(String[] args) {
        AtomicReferenceMain1 obj = new AtomicReferenceMain1();
        obj.test();
    }
}
