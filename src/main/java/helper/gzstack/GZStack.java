package helper.gzstack;

import helper.gzvector.GZVector;

import java.util.EmptyStackException;

public class GZStack<T> extends GZVector<T> {

    public GZStack() {
    }

    public T push(T item) {
        addElement(item);
        return item;
    }

    public synchronized T pop() {
        T obj;
        int len = size();

        obj = peek();
        removeElementAt(len - 1);
        return obj;
    }

    public synchronized T peek() {
        int len = size();
        if (len == 0)
            throw new EmptyStackException();
        return elementAt(len - 1);
    }

    public boolean empty() {
        return size() == 0;
    }
}
