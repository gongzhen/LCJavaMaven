package leetcode.graph;

import helper.LC133Node;
import helper.Node;
import helper.PrintUtils;
import helper.TreeUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LC133 {

    public LC133Node cloneGraph(LC133Node node) {
        return bfs(node);
    }

    private LC133Node bfs(LC133Node node) {
        if (node == null) {
            return null;
        }

        Queue<LC133Node> queue = new LinkedList<>();
        Map<LC133Node, LC133Node> map = new HashMap<>();

        queue.offer(node);

        while(!queue.isEmpty()) {
            int size = queue.size();
            for(int i = 0; i < size; ++i) {
                LC133Node _node = queue.poll();
                PrintUtils.printString("_node: " + _node.val);
                if (!map.containsKey(_node)) {
                    map.put(_node, new LC133Node(_node.val, new ArrayList<>()));
                }
                LC133Node _clone = map.get(_node);
                _node.neighbors.forEach(n -> {
                    if (!map.containsKey(n)) {
                        queue.offer(n);
                        map.put(n, new LC133Node(n.val));
                    }
                    _clone.neighbors.add(map.get(n));
                });
            }
        }
        return map.get(node);
    }

    public static void main(String[] args) {
        Supplier<LC133> supplier = () -> {
            return new LC133();
        };

        BiFunction<Supplier<LC133>, Supplier<LC133Node>, LC133Node> biFunction = (lc133Supplier, nodeSupplier) -> {
            return lc133Supplier.get().cloneGraph(nodeSupplier.get());
        };

        LC133Node node = biFunction.apply(supplier, TreeUtils.createGraphLC133());
        PrintUtils.printString("node: " + node.val);

    }
}
