package lambda;

import helper.PrintUtils;

public class AsyncCallbackMainTest {

    void send(String name, AsyncCallback<String> callback) {
        new Thread(new Runnable() {
            public void run()
            {
                // perform any operation
                System.out.println(">>>>>>>>>>>> Asynchronous Task runs");
                PrintUtils.printString("new Thread.thread:", Thread.currentThread().getName());
                // check if listener is registered.
                try {
                    Thread.sleep(10000);
                    if (callback != null) {
                        // invoke the callback method of class A
                        callback.onSuccess(Thread.currentThread().toString());
                    }
                } catch (InterruptedException e) {
                    callback.onFailure(e);
                    e.printStackTrace();
                } finally {
                    System.out.println(">>>>>>>>>>>> Asynchronous Task finished.");
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        AsyncCallbackMainTest asyncCallbackMainTest = new AsyncCallbackMainTest();
        System.out.println("Starting to perform operation in Asynchronous Task.");
        asyncCallbackMainTest.send("name", new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                PrintUtils.printString("caught : " + caught.getMessage());
            }

            @Override
            public void onSuccess(String result) {
                PrintUtils.printString("result:" + result);
            }
        });
        System.out.println("Performing operation in Asynchronous Task NOW.............");
        PrintUtils.printString("main.thread:", Thread.currentThread().getName());
    }
}
