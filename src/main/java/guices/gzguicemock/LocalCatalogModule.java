package guices.gzguicemock;

import com.google.inject.*;
import helper.PrintUtils;

public class LocalCatalogModule extends PrivateModule {

    @Override
    protected void configure() {
        install(new HealthCheckModule());
    }

    private static Injector injector_;

    public static void main(String[] args) {
        injector_ = Guice.createInjector(new LocalCatalogModule());

        int health = 157;
        int total = 2;
        if (health > 100 && health <= 200) {
            health = health / total;
        }

        PrintUtils.printString("health: " + health);
    }
}
