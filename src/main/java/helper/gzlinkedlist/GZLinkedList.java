package helper.gzlinkedlist;

import helper.gzqueue.GZQueue;


public class GZLinkedList<T> implements GZQueue<T> {
    transient int size = 0;

    transient Node<T> first;

    transient Node<T> last;

    // Queue operations.
    public T peek() {
        final Node<T> f = first;
        return (f == null) ? null : f.item;
    }

    public T poll() {
        final Node<T> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    @Override
    public T element() {
        return null;
    }

    public boolean offer(T e) {
        // return add(e);
        linkLast(e);
        return true;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int length() {
        return size;
    }

    /**
     * Links e as last element.
     * package private
     */
    void linkLast(T e) {
        final Node<T> l = last;
        final Node<T> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null) {
            first = newNode; // last is null, so there is only one node.
        } else {
            l.next = newNode;
        }
        size++;
    }

    private T unlinkFirst(Node<T> f) {
        // assert f == first && f != null;
        final T element = f.item;
        final Node<T> next = f.next;
        f.item = null;
        f.next = null; // Help GC
        first = next; // first moves to next.
        if (next == null) {
            last = null; // empty list, so first and last are null.
        } else {
            next.prev = null; // next = first, and first's prev = null
        }
        size--;
        return element;
    }

    private T unlinkLast(Node<T> l) {
        // assert l == last and l != null
        final T element = l.item;
        final Node<T> prev = l.prev;

        l.item = null;
        l.prev = null;

        last = prev;

        if (prev == null) {
            first = null; // No nodes in list if prev == null
        } else {
            prev.next = null; // prev will be the last and last.next = null;
        }
        size--;
        return element;
    }

    public boolean add(T e) {
        this.linkLast(e);
        return true;
    }

    @Override
    public T remove() {
        return this.removeFirst();
    }

    public T removeFirst() {
        GZLinkedList.Node<T> first = this.first;
        if (first == null) {
            throw null;
        } else {
            return this.unlinkFirst(first);
        }
    }

    private static class Node<T> {
        T item;
        Node<T> next;
        Node<T> prev;
        Node(Node<T> prev, T element, Node<T> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}