package guices.gzguice;

import java.util.LinkedHashMap;
import java.util.Map;

public class ToStringBuilder {
    // Linked hash map ensures ordering.
    final Map<String, Object> map = new LinkedHashMap<String, Object>();

    final String name;

    public ToStringBuilder(Class type) {
        this.name = type.getSimpleName();
    }

    public ToStringBuilder add(String name, Object value) {
        if (map.put(name, value) != null) {
            throw new RuntimeException("Duplicate names: " + name);
        }
        return this;
    }

    @Override public String toString() {
        return name + map.toString().replace('{', '[').replace('}', ']');
    }
}
