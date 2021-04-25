package guices.gzguice;

import com.google.common.collect.Lists;

import java.util.List;

final class GZInjectorShell {

    private final GZInjectorImpl injector;
    private final List<GZElement> elements;

    private GZInjectorShell(GZInjectorImpl injector) {
        this.injector = injector;
        this.elements = null;
    }

    private GZInjectorShell(GZBuilder builder, List<GZElement> elements, GZInjectorImpl injector) {
        this.elements = elements;
        this.injector = null;
    }

    GZInjectorImpl getInjector() {
        return injector;
    }

    List<GZElement> getElements() {
        return elements;
    }

    static class GZBuilder {
        private final List<GZElement> elements = Lists.newArrayList();
        private final List<GZModule> modules = Lists.newArrayList();

        private GZState state;
        private GZInjectorImpl parent;
        private GZInternalInjectorCreator.GZInjectorOptions options;

        private final Object lock = this;

        Object lock() {
            return lock;
        }

        GZBuilder parent(GZInjectorImpl parent) {
            this.parent = parent;
            return this;
        }

        GZInjectorShell.GZBuilder setInjectorOptions(GZInternalInjectorCreator.GZInjectorOptions options) {
            this.options = options;
            return this;
        }

        void addModules(Iterable<? extends GZModule> modules) {
            for (GZModule module : modules) {
                this.modules.add(module);
            }
        }

        List<GZInjectorShell> build() {
            List<GZInjectorShell> injectorShells = Lists.newArrayList();

//            injectorShells.add();
            return injectorShells;
        }
    }


}
