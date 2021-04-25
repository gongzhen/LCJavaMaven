package helper;

import java.util.HashMap;

public class MapUtils<K, V> {
    private MapUtils() {
    }

    public static <K, V> HashMap<K, V> createMap() {
        return new HashMap<>();
    }
}
