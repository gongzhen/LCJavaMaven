package helper;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class GZAbstractMap<K, V> implements GZMap<K, V> {
    transient Set<K> keySet;

    protected GZAbstractMap() {
    }

    @Override
    public int size() {
        return this.entrySet().size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    public Set<K> keySet() {
        Set<K> ks = this.keySet;
        if (ks == null) {
            ks = new GZAbstractSet<K>() {
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        private Iterator<GZEntry<K, V>> i = GZAbstractMap.this.entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return this.i.hasNext();
                        }

                        public K next() {
                            return (K) ((Map.Entry)this.i.next()).getKey();
                        }
                    };
                }

                @Override
                public int size() {
                    return GZAbstractMap.this.size();
                }
            };
            this.keySet = (Set)ks;
        }

        return (Set)ks;
    }

    public abstract Set<GZEntry<K, V>> entrySet();
}
