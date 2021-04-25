package guices.gzguice;

public abstract class GZAbstractModule implements GZModule {

    GZBinder binder;

    @Override
    public final synchronized void configure(GZBinder binder) {
        this.binder = binder;
        try {
            configure();
        } finally {
            this.binder = null;
        }
    }

    protected GZBinder binder() {
        return binder;
    }

//    protected <T> GZLinkedBindingBuilder<T> bind(Key<T> key) {
//        return binder.bind(key);
//    }

    protected <T> GZAnnotatedBindingBuilder<T> bind(Class<T> clazz) {
        return binder().bind(clazz);
    }

    protected void configure() {}

}
