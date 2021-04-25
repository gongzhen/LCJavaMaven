package lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GZPredicateTestMain {

    public static void filterList(List<Integer> list, GZPredicate<Integer> predicate) {
        list.forEach(num -> predicate.test(num));
    }

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        GZPredicate<Integer> predicate = (n) -> {
            return n % 2 == 0;
        };
        GZPredicateTestMain.filterList(list, predicate);
    }
}
