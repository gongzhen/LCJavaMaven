package guices.gzguice;

import com.google.common.collect.Maps;
import com.google.inject.spi.Dependency;

import java.util.Map;

public class GZInternalContext {
    private Map<Object, GZConstructionContext<?>> constructionContexts = Maps.newHashMap();
    private Dependency dependency;

    @SuppressWarnings("unchecked")
    public <T> GZConstructionContext<T> getConstructionContext(Object key) {
        GZConstructionContext<T> constructionContext
                = (GZConstructionContext<T>) constructionContexts.get(key);
        if (constructionContext == null) {
            constructionContext = new GZConstructionContext<T>();
            constructionContexts.put(key, constructionContext);
        }
        return constructionContext;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public Dependency setDependency(Dependency dependency) {
        Dependency previous = this.dependency;
        this.dependency = dependency;
        return previous;
    }
}
