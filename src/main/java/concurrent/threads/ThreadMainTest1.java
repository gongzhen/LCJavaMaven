package concurrent.threads;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import helper.PrintUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadMainTest1 {

    private final static StringBuilder builder;
    private final static List<String> fileNames;
    private final static String URL;
    static {
        URL = "https://www.linkedin.com/";
        final String FILEPATH = "/Users/zhengong/Project/interview/LCJavaMaven/src/main/java/concurrent/threads/folder/";
        final String FILENAME = "file";
        final String FILETYPE = ".txt";
        final int NUMBEROFFILES = 10;
        builder =  new StringBuilder();
        fileNames = new ArrayList<>();
        for (int i = 0; i < NUMBEROFFILES; i++) {
            builder.setLength(0);
            builder.append(FILEPATH + FILENAME).append(i).append(FILETYPE);
            fileNames.add(builder.toString());
        }
    }

    public static void createFiles() {
        fileNames.stream().forEach((fileName) -> {
            try {
                createFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void createFile(final String name) throws IOException {
        FilesUtil.createFile(name);
    }

    public static void writeToFiles() {
        FilesUtil.writeToFiles(fileNames, URL);
    }

    public static void readFiles() {
        FilesUtil.readFiles(fileNames);
    }

    public static void deleteFiles() {
        FilesUtil.deleteFiles(fileNames);
    }

    public static void start() {
        new Thread(() -> {
            PrintUtils.printString("createFile thread");
            createFiles();
        }).start();

        new Thread(() -> {
            PrintUtils.printString("writeToFiles thread");
            writeToFiles();
        }).start();

        new Thread(() -> {
            PrintUtils.printString("readFiles thread");
            readFiles();
        }).start();

        new Thread(() -> {
            PrintUtils.printString("deleteFiles thread");
            deleteFiles();
        }).start();
    }

    public static void main(String[] args) throws IOException {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("Thread-%d")
                .build();
        RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        ThreadPoolExecutorTest threadPoolExecutorTest = new ThreadPoolExecutorTest(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10), threadFactory, rejectedExecutionHandler, fileNames);
        try {
            threadPoolExecutorTest.submitThreads().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void executeSemaphore() {
        Semaphore createFiles = new Semaphore(1);
        Semaphore writeFiles = new Semaphore(0);
        Semaphore readFiles = new Semaphore(0);
        Semaphore deleteFiles = new Semaphore(0);
        ThreadSemaphoreTest test = new ThreadSemaphoreTest(createFiles, writeFiles, readFiles, deleteFiles, fileNames);
        test.startThread();
    }
}
