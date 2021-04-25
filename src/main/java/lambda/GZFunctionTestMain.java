package lambda;

import java.util.Arrays;
import java.util.List;

public class GZFunctionTestMain {

    public static int compute(int a, GZFunction<Integer, Integer> function1, GZFunction<Integer, Integer> function2) {
        // System.out.println("compose: " + function1.compose(function2).getClass().getInterfaces()[0]);
        return function1.compose(function2).apply(a);
    }

    public static void main(String[] args) {
        int result = GZFunctionTestMain.compute(2, value -> value * 3, value -> value * value);
        System.out.println("result: " + result);

        List<String> list = Arrays.asList("hello", "world", "hello gz");
        list.stream().map(l -> l.toUpperCase()).forEach(i -> System.out.println(i));
    }
}
