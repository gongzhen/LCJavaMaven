package helper.gzqueue;

public interface GZQueue<E> {
    boolean add(E var1);

    boolean offer(E var1);

    E remove();

    E poll();

    E element();

    E peek();
}
