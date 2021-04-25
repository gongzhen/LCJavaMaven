package guices.gzguice;

public interface GZInjector {

    void injectMembers(Object instance);

    <T> T getInstance(Class<T> type);
}
