package guices.gzguice;

import com.google.inject.Injector;
import com.google.inject.InjectorBuilder;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.internal.InternalInjectorCreator;

import java.util.Arrays;

public class GZGuice {

    private GZGuice() {};

    public static Injector createInjector(GZModule... modules) {
        return createInjector(Arrays.asList(modules));
    }

    public static Injector createInjector(Iterable<? extends GZModule> modules) {
        return createInjector(Stage.DEVELOPMENT, modules);
    }

    public static Injector createInjector(Stage stage,
                                          Iterable<? extends GZModule> modules) {
        return new GZInjectorBuilder()
                .stage(stage)
                .addModules(modules)
                .build();
    }
}
