package guices.gzguice;

import com.google.inject.*;
import com.google.inject.Module;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class GZInjectorBuilder {

    private final GZInternalInjectorCreator creator = new GZInternalInjectorCreator();

    private Stage stage = Stage.DEVELOPMENT;
    private boolean jitDisabled = false;
    private boolean allowCircularProxy = true;

    /**
     * Sets the stage for the injector. If the stage is {@link Stage#PRODUCTION},
     * singletons will be eagerly loaded when the Injector is built.
     */
    public GZInjectorBuilder stage(Stage stage) {
        this.stage = stage;
        return this;
    }

    /**
     * If explicit bindings are required, then classes that are not explicitly
     * bound in a module cannot be injected. Bindings created through a linked
     * binding (<code>bind(Foo.class).to(FooImpl.class)</code>) are allowed, but
     * the implicit binding (FooImpl) cannot be directly injected unless it is
     * also explicitly bound.
     *
     * Tools can still retrieve bindings for implicit bindings (bindings created
     * through a linked binding) if explicit bindings are required, however
     * {@link Binding#getProvider} cannot be used.
     *
     * By default, explicit bindings are not required.
     */
    public GZInjectorBuilder requireExplicitBindings() {
        this.jitDisabled = true;
        return this;
    }

    /**
     * Prevents Guice from constructing a {@link Proxy} when a circular dependency
     * is found.
     */
    public GZInjectorBuilder disableCircularProxies() {
        this.allowCircularProxy = false;
        return this;
    }

    /** Adds more modules that will be used when the Injector is created. */
    public GZInjectorBuilder addModules(Iterable<? extends GZModule> modules) {
        creator.addModules(modules);
        return this;
    }

    /** Adds more modules that will be used when the Injector is created. */
    public GZInjectorBuilder addModules(GZModule... modules) {
        creator.addModules(Arrays.asList(modules));
        return this;
    }

    /** Builds the injector. */
    public Injector build() {
        creator.injectorOptions(new GZInternalInjectorCreator.GZInjectorOptions(stage, jitDisabled, allowCircularProxy));
        return creator.build();
    }
}
