package jvm.interview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JVMThreadsMain {

    // meituan interview question:
    // https://zhuanlan.zhihu.com/p/111718642
    // https://zhuanlan.zhihu.com/p/364013921
    public static void main(String[] args) {
        new Thread(() -> {
            List<byte[]> list = new ArrayList<byte[]>();
            while (true) {
                System.out.println(new Date().toString() + Thread.currentThread() + "==");
                byte[] b = new byte[1024 * 1024 * 100];
                list.add(b);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // thread 2
        new Thread(() -> {
            while (true) {
                System.out.println(new Date().toString() + Thread.currentThread() + "==");
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
