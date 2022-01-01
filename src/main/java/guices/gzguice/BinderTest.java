package guices.gzguice;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Provider;

public class BinderTest {

    public void testProviderFromBinder() {
        Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                fooProvider = binder.getProvider(Foo.class);
                fooProvider.get();
            }
        });

    }

    static class Foo {}

    Provider<Foo> fooProvider;

    public static void main(String[] args) {

    }
}
