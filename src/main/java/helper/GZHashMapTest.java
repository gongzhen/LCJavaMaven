package helper;

import java.util.*;

public class GZHashMapTest {

    public static void main(String[] args) {
        TestAbstractSet testAbstractSet = new TestAbstractSet();
        PrintUtils.printString("testAbstractSet.keySet():", testAbstractSet.keySet());

        GZMap gzMap = new GZHashMap<String, String>(10, 0.75f);
        gzMap.put("A", "aaa");
        gzMap.put("B", "bbb");
        gzMap.put("C", "ccc");
        gzMap.put("C", "cccc");

//        PrintUtils.printString("keySet:", gzMap.keySet());
        Set<Integer> sets = gzMap.keySet();

        Map map = new HashMap<String, String>();
        map.put("A", "aaa");
        map.put("B", "bbb");
        map.put("C", "ccc");
        map.put("C", "cccc");
        PrintUtils.printString("keySet:", map.keySet());
    }

    private static class TestAbstractSet {
        public Set<Integer> keySet() {
            final List<Integer> list = new ArrayList<>();
            list.add(1);
            list.add(2);
            list.add(3);

            Set<Integer> keySet = new AbstractSet<Integer>() {

                // AbstractCollection iterator();
                @Override
                public Iterator<Integer> iterator() {
                    return new Iterator<Integer>() {
                        private Iterator<Integer> itr = list.iterator();

                        @Override
                        public boolean hasNext() {
                            return itr.hasNext();
                        }

                        @Override
                        public Integer next() {
                            return itr.next();
                        }
                    };
                }

                // AbstractCollection iterator();
                @Override
                public int size() {
                    return 0;
                }
            };
            return keySet;
        }
    }
}
