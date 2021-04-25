package helper;

import java.util.LinkedList;
import java.util.Queue;

public class QueueUtils<T> {

    private QueueUtils() {}

    public static <T> Queue<T> createQueue() {
        return new LinkedList<T>();
    }
}
