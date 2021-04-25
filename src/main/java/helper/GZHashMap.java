package helper;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GZHashMap<K, V> extends GZAbstractMap<K, V> implements GZMap<K, V> {

    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    static final int MAXIMUM_CAPACITY = 1 << 30;

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

        return null;
    }

    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean envict) {
        // define temp table tab to hold table's value. define Node p,
        GZNode[] tab = table;
        GZNode<K, V> p;
        int i;
        // define tab size n and idex int i.
        int n = tab.length;
        // check tab is null or tab.length is 0, then set tab from resize() and n as tab's length.
        if (tab == null || tab.length == 0) {
            //@todo reset tab = resize()
            // n = tab.length()
        }
        // get the index: i = (n - 1) & hash
        // check tab[i] == null, if true, then tab[i] = new GZNode()
        i = (n - 1) & hash;
        p = tab[i];
        K k;
        if (tab[i] == null) {
            tab[i] = new GZNode(hash, key, value, null);
        } else {
            // if tab[i] is not null, then check if
            // tab[i].hash == the hash AND (p.key == key OR (key != null && p.key.equals(key)))
            p = tab[i];
            if (p.hash == hash && ((k = p.key) == key || (k != null && key.equals(k)))) {

            }

        }

        i = (n-1) & hash;
        p = tab[i];
        if (p == null) {
            // if tab's value from index (n-1) & hash is null
            // then insert value to index from tab
            tab[i] = new GZNode<>(hash, key, value, null);
        } else {
            // set binCount = 0
            // check e = p.next;
            // if e == null, then set p.next = new GZNode();
            // if e != null, then compare
            // 1: e.hash and hash, if e.hash == hash, then break and find the target e
            // AND
            // 2: compare e.key == key, OR compare (key != null AND compare actual value key and e.key)
            // then break,
            // p move to e
            // binCount++
            int binCount = 0;
            GZNode e = p.next;


        }


        // not null, it means there is value in this index already.
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

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    static class GZNode<K,V> implements GZMap.GZEntry<K,V> {
        final int hash;
        final K key;
        V value;
        GZNode<K,V> next;

        GZNode(int hash, K key, V value, GZNode<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return value;
        }

        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }


        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    static final int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
}
