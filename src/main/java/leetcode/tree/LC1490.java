package leetcode.tree;

import helper.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class LC1490 {

    public Node cloneTree(Node root) {
        // return dfs(root);
        return bfs(root);
    }

    private Node dfs(Node node) {
        if (node == null) {
            return null;
        }
        List<Node> children = node.children;;
        Node clone = new Node(node.val, new ArrayList<>());

        children.stream().forEach((n) -> {
            Node cloneChild = new Node(n.val, new ArrayList<>());
            clone.children.add(dfs(n));
        });
        return clone;
    }

    private Node bfs(Node node) {
        if (node == null) {
            return null;
        }

        HashMap<Node, Node> map = MapUtils.createMap();
        Queue<Node> queue = QueueUtils.createQueue();
        queue.offer(node);

        while(!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node _node = queue.poll();
                if (!map.containsKey(_node)) {
                    map.put(_node, new Node(_node.val, new ArrayList<>()));
                }
                // Node cloneNode = map.get(_node);
                _node.children.forEach(n -> {
                    map.put(n, new Node(n.val, new ArrayList<>()));
                    map.get(_node).children.add(map.get(n));
                    queue.offer(n);
                });
            }
        }
        return map.get(node);
    }

    public static void main(String[] args) {
        Supplier<LC1490> supplier = LC1490::new;
        Node node = supplier.get().cloneTree(TreeUtils.createTreeLC1490().get());
        printNode(node);
    }

    public static void printNode(Node node) {
        PrintUtils.printString("node: " + node.val);
        node.children.forEach(n -> {
            printNode(n);
        });
    }
}
