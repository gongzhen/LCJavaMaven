package asynccallback;

import helper.PrintUtils;

public class AsynCallbackMain {

    public static void main(String[] args) {
        AsynCallbackMain bootstrap = new AsynCallbackMain();

        Worker worker = bootstrap.newWorker();

        Helper wrapper = new Helper();
        wrapper.setWorker(worker);
        wrapper.setParam("hello");

        PrintUtils.printString("startAsyncWork:", Thread.currentThread().getName());
        bootstrap.startAsyncWork(wrapper).addListener(new Listener() {
            @Override
            public void handleResult(Object result) {
                PrintUtils.printString("addListener.currentThread:", Thread.currentThread().getName());
                PrintUtils.printString("addListener.currentThread.result:", result);
                PrintUtils.printString("addListener.currentThread is over.>>>>>>>>>>>>>");
            }
        });

        PrintUtils.printString("AsynCallbackMain.main.threadname:", Thread.currentThread().getName());
        PrintUtils.printString("AsynCallbackMain.main.threadname is over.>>>>>>>>>>>>>");
    }

    /**
     * sequential call.
     * AsynCallbackMain.main.namemain
     * doWork.worker:asynccallback.AsynCallbackMain$2@998af42
     * AsynCallbackMain.main.name is over.>>>>>>>>>>>>>
     */
    private Helper startAsyncWork(Helper helper) {
        new Thread(() -> {
            Worker worker = helper.getWorker();
            PrintUtils.printString("startAsyncWork.currentThread:", Thread.currentThread().getName());
            String result = worker.start(helper.getParam());
            PrintUtils.printString("doWork.handleResult.result:", result);
            helper.getListener().handleResult(result);
        }).start();

        return helper;
    }

    private Worker newWorker() {
        return new Worker() {
            @Override
            public String start(Object object) {
                try {
                    PrintUtils.printString("newWorker sleep 4 seconds.....:", Thread.currentThread().getName());
                    Thread.sleep(5000);
                    PrintUtils.printString("newWorker sleep 4 seconds done.>>>>>>>>>>>>>>.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return object + " world"; // appending object: hello + world.
            }
        };
    }
}
