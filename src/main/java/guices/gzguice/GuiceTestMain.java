package guices.gzguice;

import annotations.GZNonNull;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import helper.PrintUtils;

public class GuiceTestMain {

    @GZNonNull
    private GuiceTestMain guiceTestMain;

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(BlueFoo.class);
            }
        });
        BlueFoo foo = injector.getInstance(BlueFoo.class);
        PrintUtils.printString(foo.toString());

//        Injector gzInjector = GZGuice.createInjector(new AbstractModule(){
//
//            @Override
//            protected void configure() {
//                bind(BlueFoo.class);
//            }
//        });
//
//        foo = gzInjector.getInstance(BlueFoo.class);
//        PrintUtils.printString(foo.toString());
    }



    static class BlueFoo {

    }

}
