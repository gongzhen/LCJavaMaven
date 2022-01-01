package concurrent.threads;

import helper.PrintUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ThreadSemaphoreTest {

    private final static String URL = "https://www.linkedin.com/";
    private final List<String> fileNames;

    private Semaphore createFileSemaphore;
    private Semaphore writeFileSemaphore;
    private Semaphore readFileSemaphore;
    private Semaphore deleteFileSemaphore;

    ThreadSemaphoreTest(final Semaphore createFileSemaphore,
                               final Semaphore writeFileSemaphore,
                               final Semaphore readFileSemaphore,
                               final Semaphore deleteFileSemaphore, final List<String> fileNames) {
        this.createFileSemaphore = createFileSemaphore;
        this.writeFileSemaphore = writeFileSemaphore;
        this.readFileSemaphore = readFileSemaphore;
        this.deleteFileSemaphore = deleteFileSemaphore;
        this.fileNames = fileNames;
    }

    public void startThread() {
        Thread t1 = new CreateFileThread(() -> {}, this.fileNames);
        Thread t2 = new WriteFileThread(() -> {}, this.fileNames);
        Thread t3 = new ReadFileThread(() -> {}, this.fileNames);
        Thread t4 = new DeleteFileThread(() -> {}, this.fileNames);

        List<Thread> threads = Arrays.asList(t1, t2, t3, t4);
        threads.forEach(thread -> {
            thread.start();
        });

        threads.forEach(thread -> {
            try {
                PrintUtils.printString("thread:" + thread.getName() + " join.");
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private class DeleteFileThread extends Thread {
        private Runnable createRunnable;
        private final List<String> fileNames;

        public DeleteFileThread(Runnable runnable, List<String> fileNames) {
            this.createRunnable = runnable;
            this.fileNames = fileNames;
        }

        @Override
        public void run() {
            try {
                deleteFileSemaphore.acquire();
                FilesUtil.deleteFiles(this.fileNames);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                deleteFileSemaphore.release();
            }

        }
    }

    private class ReadFileThread extends Thread {
        private Runnable createRunnable;
        private final List<String> fileNames;

        public ReadFileThread(Runnable runnable, List<String> fileNames) {
            this.createRunnable = runnable;
            this.fileNames = fileNames;
        }

        @Override
        public void run() {
            try {
                readFileSemaphore.acquire();
                FilesUtil.readFiles(this.fileNames);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                deleteFileSemaphore.release();
            }
        }
    }

    private class WriteFileThread extends Thread {
        private Runnable createRunnable;
        private final List<String> fileNames;

        public WriteFileThread(Runnable runnable, List<String> fileNames) {
            this.createRunnable = runnable;
            this.fileNames = fileNames;
        }

        @Override
        public void run() {
            try {
                PrintUtils.printString("before acquire writeFileSemaphore.state: " + writeFileSemaphore.availablePermits());
                writeFileSemaphore.acquire();
                PrintUtils.printString("after acquire writeFileSemaphore.state: " + writeFileSemaphore.availablePermits());
                FilesUtil.writeToFiles(this.fileNames, URL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                PrintUtils.printString("116 before relase readFileSemaphore.state: " + readFileSemaphore.availablePermits());
                readFileSemaphore.release();
                PrintUtils.printString("116 after relase readFileSemaphore.state: " + readFileSemaphore.availablePermits());
            }

        }
    }

    private class CreateFileThread extends Thread {
        private Runnable createRunnable;
        private final List<String> fileNames;

        public CreateFileThread(Runnable runnable, List<String> fileNames) {
            this.createRunnable = runnable;
            this.fileNames = fileNames;
        }

        @Override
        public void run() {
            try {
                PrintUtils.printString("before acquire createFileSemaphore.state: " + createFileSemaphore.availablePermits());
                createFileSemaphore.acquire();
                PrintUtils.printString("after acquire createFileSemaphore.state: " + createFileSemaphore.availablePermits());
                fileNames.stream().forEach((fileName) -> {
                    createFile(fileName);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                PrintUtils.printString("145 before relase writeFileSemaphore.state: " + writeFileSemaphore.availablePermits());
                writeFileSemaphore.release();
                PrintUtils.printString("145 after acquire writeFileSemaphore.state: " + writeFileSemaphore.availablePermits());
            }
        }

        private void createFile(final String name) {
            FilesUtil.createFile(name);
        }
    }
}
