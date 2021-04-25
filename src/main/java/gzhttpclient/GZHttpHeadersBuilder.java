package gzhttpclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GZHttpHeadersBuilder {

    private final TreeMap<String, List<String>> headersMap;

    public GZHttpHeadersBuilder() {
        this.headersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void addHeader(String name, String value) {
        this.headersMap.computeIfAbsent(name, (k) -> new ArrayList(1)).add(value);
    }

    public void setHeader(String name, String value) {
        List<String> values = new ArrayList(1);
        values.add(value);
        this.headersMap.put(name, values);
    }

    public Map<String, List<String>> map() {
        return this.headersMap;
    }
}
