package gzhttpclient;

import java.util.*;
import java.util.function.BiPredicate;

public class GZHttpHeaders {

    private static final GZHttpHeaders NO_HEADERS = new GZHttpHeaders(Map.of());
    private final Map<String, List<String>> headers;

    private GZHttpHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public static GZHttpHeaders of(Map<String, List<String>> headerMap, BiPredicate<String, String> filter) {
        Objects.requireNonNull(headerMap);
        Objects.requireNonNull(filter);
        return headersof(headerMap, filter);
    }

    private static GZHttpHeaders headersof(Map<String, List<String>> map, BiPredicate<String, String> filter) {
        TreeMap<String, List<String>> other = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        TreeSet<String> notAdded = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        ArrayList<String> tempList = new ArrayList<>();
        map.forEach((key, value) -> {
            String headerName = Objects.requireNonNull(key).trim();
            if (headerName.isEmpty()) {
                throw new IllegalArgumentException("empty key");
            } else {
                List<String> headerValues = Objects.requireNonNull(value);
                headerValues.forEach((headerValue) -> {
                    headerValue = Objects.requireNonNull(headerValue).trim();
                    if (filter.test(headerName, headerValue)) {
                        tempList.add(headerValue);
                    }
                });

                if (tempList.isEmpty()) {
                    if (other.containsKey(headerName) || notAdded.contains(headerName.toLowerCase(Locale.ROOT))) {
                        throw new IllegalArgumentException("duplicate key" + headerName);
                    }

                    notAdded.add(headerName.toLowerCase(Locale.ROOT));
                } else if (other.put(headerName, List.copyOf(tempList)) != null) {
                    throw new IllegalArgumentException("duplicate key" + headerName);
                }

                tempList.clear();
            }
        });
        return other.isEmpty() ? NO_HEADERS : new GZHttpHeaders(Collections.unmodifiableMap(other));
    }
}
