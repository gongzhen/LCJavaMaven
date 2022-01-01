package concurrent.threads;

import helper.PrintUtils;

import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolExecutorTest {

    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private TimeUnit unit;
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler handler;
    private final static String URL = "https://www.linkedin.com/";
    private final List<String> fileNames;

    private ThreadPoolExecutor threadPoolExecutor;

    private class DeleteFileThread implements Runnable {
        private final List<String> fileNames;

        public DeleteFileThread(List<String> fileNames) {
            this.fileNames = fileNames;
        }

        @Override
        public void run() {
            FilesUtil.deleteFiles(this.fileNames);
        }
    }

    private class ReadFileThread implements Runnable {
        private final List<String> fileNames;

        public ReadFileThread(List<String> fileNames) {
            this.fileNames = fileNames;
        }

        @Override
        public void run() {
            FilesUtil.readFiles(this.fileNames);
        }
    }

    private class WriteFileThread implements Runnable {
        private final List<String> fileNames;

        public WriteFileThread(List<String> fileNames) {
            this.fileNames = fileNames;
            PrintUtils.printString("WriteFileThread");
        }

        @Override
        public void run() {
            FilesUtil.writeToFiles(this.fileNames, URL);
        }
    }

    private class CreateFileThread implements Runnable {
        private final List<String> fileNames;

        public CreateFileThread(List<String> fileNames) {
            this.fileNames = fileNames;
            PrintUtils.printString("CreateFileThread");
        }

        @Override
        public void run() {
            fileNames.stream().forEach((fileName) -> {
                PrintUtils.printString("file:" + fileName);
                FilesUtil.createFile(fileName);
            });
        }
    }

    public ThreadPoolExecutorTest(final int corePoolSize,
                                  final int maximumPoolSize,
                                  final long keepAliveTime,
                                  final TimeUnit unit,
                                  final BlockingQueue<Runnable> workQueue,
                                  final ThreadFactory threadFactory,
                                  final RejectedExecutionHandler handler,
                                  final List<String> fileNames) {
        this.fileNames = fileNames;
        this.threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler);
    }

    public CompletableFuture<Void>  submitThreads() {
        CreateFileThread t1 = new CreateFileThread(this.fileNames);
        WriteFileThread t2 = new WriteFileThread(this.fileNames);
        ReadFileThread t3 = new ReadFileThread(this.fileNames);
        DeleteFileThread t4 = new DeleteFileThread(this.fileNames);

        return CompletableFuture
                .runAsync(t1, this.threadPoolExecutor)
                .thenRunAsync(t2, this.threadPoolExecutor)
                .thenRunAsync(t3, this.threadPoolExecutor)
                .thenRunAsync(t4, this.threadPoolExecutor)
                .whenComplete((v, t) -> {
                    PrintUtils.printString("whenComplete:" + v);
                });
    }




}
