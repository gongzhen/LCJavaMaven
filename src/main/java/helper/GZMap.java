package helper;

//import java.util.ImmutableCollections.AbstractImmutableMap;
import java.io.Serializable;
import java.util.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface GZMap<K, V> {
    int size();

    boolean isEmpty();

    boolean containsKey(Object var1);

    boolean containsValue(Object var1);

    V get(K key);

    V put(K key, V value);

    V remove(Object var1);

    void putAll(Map<? extends K, ? extends V> var1);

    void clear();

    Set<K> keySet();

    Collection<V> values();

    Set<GZMap.GZEntry<K, V>> entrySet();

    boolean equals(Object var1);

    int hashCode();

    default V getOrDefault(K key, V defaultValue) {
        Object v;
        return (v = this.get(key)) == null && !this.containsKey(key) ? defaultValue : (V) v;
    }

    default void forEach(BiConsumer<Object, Object> action) {
        Objects.requireNonNull(action);

        Object k;
        Object v;
        for(Iterator var2 = this.entrySet().iterator(); var2.hasNext(); action.accept(k, v)) {
            Map.Entry entry = (Map.Entry)var2.next();

            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException var7) {
                throw new ConcurrentModificationException(var7);
            }
        }

    }

    default void replaceAll(BiFunction<Object, Object, Object> function) {
        Objects.requireNonNull(function);
        Iterator var2 = this.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry entry = (Map.Entry)var2.next();

            Object k;
            Object v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException var8) {
                throw new ConcurrentModificationException(var8);
            }

            v = function.apply(k, v);

            try {
                entry.setValue(v);
            } catch (IllegalStateException var7) {
                throw new ConcurrentModificationException(var7);
            }
        }

    }

    default V putIfAbsent(K key, V value) {
        V v = this.get(key);
        if (v == null) {
            v = this.put(key, value);
        }

        return v;
    }

    default boolean remove(K key, V value) {
        Object curValue = this.get(key);
        if (Objects.equals(curValue, value) && (curValue != null || this.containsKey(key))) {
            this.remove(key);
            return true;
        } else {
            return false;
        }
    }

    default boolean replace(K key, V oldValue, V newValue) {
        Object curValue = this.get(key);
        if (Objects.equals(curValue, oldValue) && (curValue != null || this.containsKey(key))) {
            this.put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    default V replace(K key, V value) {
        Object curValue;
        if ((curValue = this.get(key)) != null || this.containsKey(key)) {
            curValue = this.put(key, value);
        }

        return (V) curValue;
    }

    default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        Object v;
        Object newValue;
        if ((v = this.get(key)) == null && (newValue = mappingFunction.apply(key)) != null) {
            this.put(key, (V) newValue);
            return (V) newValue;
        } else {
            return (V) v;
        }
    }

    default Object computeIfPresent(K key, BiFunction<K, Object, Object> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Object oldValue;
        if ((oldValue = this.get(key)) != null) {
            Object newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                this.put(key, (V) newValue);
                return newValue;
            } else {
                this.remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
            if (oldValue == null && !this.containsKey(key)) {
                return null;
            } else {
                this.remove(key);
                return null;
            }
        } else {
            this.put(key, newValue);
            return newValue;
        }
    }

    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = this.get(key);
        V newValue = oldValue == null ? value : remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            this.remove(key);
        } else {
            this.put(key, newValue);
        }

        return newValue;
    }

//    static <K, V> Map<K, V> of() {
//        return ImmutableCollections.emptyMap();
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1) {
//        return new ImmutableCollections.Map1(k1, v1);
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2});
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2, k3, v3});
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2, k3, v3, k4, v4});
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2, k3, v3, k4, v4, k5, v5});
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6});
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7});
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8});
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9});
//    }
//
//    static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
//        return new ImmutableCollections.MapN(new Object[]{k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10});
//    }
//
//    @SafeVarargs
//    static <K, V> Map<K, V> ofEntries(Map.Entry<? extends K, ? extends V>... entries) {
//        if (entries.length == 0) {
//            return ImmutableCollections.emptyMap();
//        } else if (entries.length == 1) {
//            return new ImmutableCollections.Map1(entries[0].getKey(), entries[0].getValue());
//        } else {
//            Object[] kva = new Object[entries.length << 1];
//            int a = 0;
//            Map.Entry[] var3 = entries;
//            int var4 = entries.length;
//
//            for(int var5 = 0; var5 < var4; ++var5) {
//                Map.Entry<? extends K, ? extends V> entry = var3[var5];
//                kva[a++] = entry.getKey();
//                kva[a++] = entry.getValue();
//            }
//
//            return new ImmutableCollections.MapN(kva);
//        }
//    }
//
//    static <K, V> Map.Entry<K, V> entry(K k, V v) {
//        return new KeyValueHolder(k, v);
//    }

//    static <K, V> Map<K, V> copyOf(Map<? extends K, ? extends V> map) {
//        return map instanceof ImmutableCollections.AbstractImmutableMap ? map : ofEntries((Map.Entry[])map.entrySet().toArray(new Map.Entry[0]));
//    }

    interface GZEntry<K, V> {
        K getKey();

        V getValue();

        V setValue(V value);

        boolean equals(Object o);

        int hashCode();

        public static <K extends Comparable<? super K>, V> Comparator<GZMap.GZEntry<K, V>> comparingByKey() {
            return (Comparator<GZMap.GZEntry<K, V>> & Serializable)
                    (c1, c2) -> c1.getKey().compareTo(c2.getKey());
        }

        public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue() {
            return (Comparator<Map.Entry<K, V>> & Serializable)
                    (c1, c2) -> c1.getValue().compareTo(c2.getValue());
        }

        public static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
            Objects.requireNonNull(cmp);
            return (Comparator<Map.Entry<K, V>> & Serializable)
                    (c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
        }

        public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
            Objects.requireNonNull(cmp);
            return (Comparator<Map.Entry<K, V>> & Serializable)
                    (c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
        }
    }
}
