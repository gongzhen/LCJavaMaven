package helper;

import lambda.GZConsumer;
import java.util.*;

public class GZHashMap<K, V> extends GZAbstractMap<K, V> implements GZMap<K, V> {

    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /* ---------------- Fields -------------- */
    transient GZNode<K,V>[] table;
    transient Set<GZMap.GZEntry<K,V>> entrySet;
    transient int size;
    transient int modCount;
    int threshold;
    final float loadFactor;

    public GZHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object var1) {
        return false;
    }

    @Override
    public boolean containsValue(Object var1) {
        return false;
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean envict) {
        // define temp table tab to hold table's value. define Node p,
        GZNode[] tab;
        GZNode<K, V> p;
        int i;
        // define tab size n and idex int i.
        int n;
        // check tab is null or tab.length is 0, then set tab from resize() and n as tab's length.
        if ((tab = table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }
        if ((p = tab[i = (n - 1) & hash]) == null) {
            tab[i] = new GZNode(hash, key, value, null);
        } else {
            GZNode<K, V> e; K k;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k)))) {
                e = p;
            }
            //@todo putTreeVal.
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        // @todo treeifyBin.
                        break;
                    }

                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k)))) {
                        break;
                    }
                    p = e;
                }
            }
            if (e != null) {
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null) {
                    e.value = value;
                }

                return oldValue;
            }
        }

        ++modCount;
        if (++size > threshold) {
            resize();
        }
        return null;
    }

    @Override
    public V remove(Object var1) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> var1) {

    }

    @Override
    public void clear() {

    }

    // https://www.cnblogs.com/dsj2016/p/5551059.html
    @Override
    public Set<K> keySet() {
//        Set<K> ks;
//        return (ks = keySet) == null ? (keySet = new KeySet()) : ks;

        Set<K> ks = this.keySet;
        if (ks == null) {
            ks = new GZHashMap.KeySet();
            this.keySet = (Set)ks;
        }
        return (Set)ks;
    }

    final class KeySet extends GZAbstractSet<K> {
        KeySet() {
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return size;
        }

        public final void forEach(GZConsumer<? super K> action) {
            GZNode<K, V>[] tab;
            if (action == null) {
                throw new NullPointerException();
            }

            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; i++) {
                    for (GZNode<K, V> e = tab[i]; e != null; e = e.next) {
                        action.accept(e.key);
                    }

                    if (modCount != mc) {
                        throw new ConcurrentModificationException();
                    }
                }
            }
        }
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<GZMap.GZEntry<K, V>> entrySet() {
        return null;
    }

    // Android: https://android.googlesource.com/platform/libcore.git/+/android-3.2.4_r1/luni/src/main/java/java/util/HashMap.java
    // jdk7: https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/util/HashMap.java
    // jdk10: http://hg.openjdk.java.net/jdk10/jdk10/jdk/file/ffa11326afd5/src/java.base/share/classes/java/util/HashMap.java
    final class KeyIterator extends HashIterator implements Iterator<K> {
        @Override
        public K next() {
            return nextNode().key;
        }
    }

    abstract class HashIterator {
        GZNode<K,V> next;        // next entry to return
        GZNode<K,V> current;     // current entry
        int expectedModCount;  // for fast-fail
        int index;             // current slot

        HashIterator() {
            expectedModCount = modCount;
            GZNode<K,V>[] t = table;
            current = next = null;
            index = 0;
            if (t != null && size > 0) { // advance to first entry
                do {} while (index < t.length && (next = t[index++]) == null);
            }
        }

        public boolean hasNext() {
            return next != null;
        }

        final GZNode<K, V> nextNode() {
            GZNode<K,V>[] t;
            GZNode<K,V> e = next;
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }

            if (e == null)
                throw new NoSuchElementException();
            if ((next = (current = e).next) == null && (t = table) != null) {
                do {} while (index < t.length && (next = t[index++]) == null);
            }
            return e;
        }
    }

    static final int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    static class GZNode<K, V> implements GZMap.GZEntry<K, V> {
        final int hash;
        final K key;
        V value;
        GZNode<K, V> next;

        GZNode(int hash, K key, V value, GZNode<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GZNode<?, ?> node = (GZNode<?, ?>) o;
            return hash == node.hash &&
                    Objects.equals(key, node.key) &&
                    Objects.equals(value, node.value) &&
                    Objects.equals(next, node.next);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hash, key, value, next);
        }
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    GZNode<K, V> newNode(int hash, K key, V value, GZNode<K, V> next) {
        return new GZNode<>(hash, key, value, next);
    }

    final GZNode<K, V>[] resize() {
        GZNode<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        GZNode<K,V>[] newTab = (GZNode<K,V>[])new GZNode[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                GZNode<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
//                    else if (e instanceof TreeNode)
//                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        GZNode<K,V> loHead = null, loTail = null;
                        GZNode<K,V> hiHead = null, hiTail = null;
                        GZNode<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
}
