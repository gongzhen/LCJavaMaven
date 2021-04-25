package guices.gzguice;

public interface GZBinder {

    <T> GZAnnotatedBindingBuilder<T> bind(Class<T> clazz);

    void install(GZModule gzModule);

}
