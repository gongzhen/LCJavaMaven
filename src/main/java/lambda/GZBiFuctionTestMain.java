package lambda;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GZBiFuctionTestMain {

    public static int compute(int a, int b, GZBiFuction<Integer, Integer, Integer> biFuction) {
        return biFuction.apply(a, b);
    }

    public static void main(String[] args) {
        int res = GZBiFuctionTestMain.compute(3, 4, (a, b) -> a + b);
        System.out.println("res: " + res);

        List<String> names = Arrays.asList(
                "Peter",
                "Martin",
                "John",
                "Vijay",
                "Arthur"
        );

        names.stream().map(Function.identity()).forEach(System.out::println);
        names.stream().map(e -> e).forEach(System.out::println);
    }
}
