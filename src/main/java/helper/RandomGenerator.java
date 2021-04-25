package helper;

import java.util.Random;
import java.util.function.Supplier;

public class RandomGenerator {

    private final Supplier<? extends Random> randomSupplier;

    public RandomGenerator(Supplier<? extends Random> randomSupplier) {
        this.randomSupplier = randomSupplier;
    }


}
