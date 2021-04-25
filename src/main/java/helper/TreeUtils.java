package helper;

import leetcode.tree.LC1490;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeUtils {

    public static void printTree(TreeNode node) {
        dfs(node);
    }

    public static void printTreePretty(TreeNode node) {
        if (node == null) {
            return;
        }
        int height = heightOfTree(node);
        int row = height;
        int col = row * 2 + 1;
        int[][] matrix = new int[row][col];
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(node);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for(int i = 0; i < size; i++) {
                TreeNode n = queue.poll();
                if (n != null) {
                    printSpaceForLevel(height);
                    PrintUtils.printStringWithoutNewLine("" + n.val);
                } else {
                    printSpaceForLevel(height);
                    PrintUtils.printStringWithoutNewLine("#");
                }
                if(n != null && n.left != null) {
                    queue.offer(n.left);
                } else {
                    queue.offer(null);
                }
                if(n != null && n.right != null) {
                    queue.offer(n.right);
                } else {
                    queue.offer(null);
                }
            }
            PrintUtils.printString("");
            height--;
            if (height == -1) {
                break;
            }
        }
    }

    private static void printSpaceForLevel(int level) {
        for(int i = 0; i < level * 2 + 2; i++) {
            PrintUtils.printStringWithoutNewLine(" ");
        }
    }

    private static int heightOfTree(TreeNode node) {
        if (node == null) {
            return 0;
        }

        return Math.max(heightOfTree(node.left), heightOfTree(node.right)) + 1;
    }

    public static void printTreeByLevel(TreeNode node) {
        if(node == null) return;
        Queue<TreeNode> pq = new LinkedList<>();
        pq.offer(node);
        while (!pq.isEmpty()) {
            int size = pq.size();
            for(int i = 0; i < size; i++) {
                TreeNode n = pq.poll();
                PrintUtils.printStringWithoutNewLine("" + n.val + ", ");
                if(n.left != null) {
                    pq.offer(n.left);
                }
                if(n.right != null) {
                    pq.offer(n.right);
                }
                PrintUtils.printString("");
            }
        }
    }

    public static void dfs(TreeNode node) {
        if(node == null) {
            return;
        }
        dfs(node.left);
        PrintUtils.printStringWithoutNewLine("" + node.val + ", ");
        dfs(node.right);
    }

    public static TreeNode createTree() {
        //       3
        //    2      7
        //  1   5       10
        TreeNode node5 = new TreeNode(5);
        TreeNode node2 = new TreeNode(2);
        TreeNode node1 = new TreeNode(1);
        TreeNode node7 = new TreeNode(7);
        TreeNode node3 = new TreeNode(3);
        TreeNode node10 = new TreeNode(10);
        node3.left = node2;
        node3.right = node7;
        node2.left = node1;
        node2.right = node5;
        node7.right = node10;
        return node3;
    }

    public static TreeNode LC156createTree() {
        //       1
        //    2     3
        //  4   5
        TreeNode node1 = new TreeNode(1);
        TreeNode node2 = new TreeNode(2);
        TreeNode node3 = new TreeNode(3);
        TreeNode node4 = new TreeNode(4);
        TreeNode node5 = new TreeNode(5);
        node1.left = node2;
        node1.right = node3;
        node2.left = node4;
        node2.right = node5;
        return node1;
    }

    public static Map LC863createTree() {
        Map<TreeNode, TreeNode> pair = new HashMap<>();
        TreeNode node3 = new TreeNode(3);
        TreeNode node5 = new TreeNode(5);
        TreeNode node1 = new TreeNode(1);
        TreeNode node6 = new TreeNode(6);
        TreeNode node2 = new TreeNode(2);
        TreeNode node7 = new TreeNode(7);
        TreeNode node4 = new TreeNode(4);
        TreeNode node0 = new TreeNode(0);
        TreeNode node8 = new TreeNode(8);
        node3.left = node5;
        node3.right = node1;

        node1.left = node0;
        node1.right = node8;

        node5.left = node6;
        node5.right = node2;
        node2.left = node7;
        node2.right = node4;

        pair.put(node3, node5);
        return pair;
    }

    public static TreeNode LC1022createTree() {
        //       1
        //    0      1
        //  0   1   0  1
        TreeNode node1 = new TreeNode(1);
        TreeNode node00 = new TreeNode(0);
        TreeNode node01 = new TreeNode(1);
        TreeNode node000 = new TreeNode(0);
        TreeNode node001 = new TreeNode(1);
        TreeNode node010 = new TreeNode(0);
        TreeNode node011 = new TreeNode(1);

        node1.left = node00;
        node1.right = node01;

        node01.left = node010;
        node01.right = node011;

        node00.left = node000;
        node00.right = node001;

        return node1;
    }

    public static TreeNode createBSTTree() {
        //       5
        //    2      7
        //  1   3      10
        TreeNode node5 = new TreeNode(5);
        TreeNode node2 = new TreeNode(2);
        TreeNode node1 = new TreeNode(1);
        TreeNode node7 = new TreeNode(7);
        TreeNode node3 = new TreeNode(3);
        TreeNode node10 = new TreeNode(10);
        node5.left = node2;
        node5.right = node7;
        node2.left = node1;
        node2.right = node3;
        node5.right = node7;
        node7.right = node10;
        return node5;
    }

    public static TreeNode LC298createBSTTree() {
        //       5
        //    2      7
        //  1   3      10
        TreeNode node2 = new TreeNode(2);
        TreeNode node1 = new TreeNode(1);
        TreeNode node22 = new TreeNode(2);
        TreeNode node3 = new TreeNode(3);
        node2.left = null;
        node2.right = node3;
        node3.left = node22;
        node22.left = node1;
        return node2;
    }

    public static TreeNode LC988createBSTTree() {
        //       2
        //    2      1
        //   N 1    0 N
        //    0
        TreeNode node2 = new TreeNode(2);
        TreeNode node22 = new TreeNode(2);
        TreeNode node1 = new TreeNode(1);
        TreeNode node11 = new TreeNode(1);
        TreeNode node0 = new TreeNode(0);
        TreeNode node00 = new TreeNode(0);

        node2.left = node22;
        node2.right = node1;
        node22.right = node11;
        node1.left = node0;
        node11.left = node00;
        return node2;
    }

    public static TreeNode LC270createBSTTree() {
/**
 *
 * Input: root = [4,2,5,1,3], target = 3.714286
 *
 *     4
 *    / \
 *   2   5
 *  / \
 * 1   3
 *
 * Output: 4
 * */
        TreeNode node2 = new TreeNode(2);
        TreeNode node1 = new TreeNode(1);
        TreeNode node4 = new TreeNode(4);
        TreeNode node5 = new TreeNode(5);
        TreeNode node3 = new TreeNode(3);
        node4.left = node2;
        node4.right = node5;
        node2.left = node1;
        node2.right = node3;
        return node4;
    }

    public static TreeNode LC230createBSTTree() {
        //        5
        //     3      6
        //   2   4
        // 1

        TreeNode node5 = new TreeNode(5);
        TreeNode node2 = new TreeNode(2);
        TreeNode node1 = new TreeNode(1);
        TreeNode node6 = new TreeNode(6);
        TreeNode node3 = new TreeNode(3);
        TreeNode node4 = new TreeNode(4);
        node5.left = node3;
        node5.right = node6;
        node3.left = node2;
        node3.right = node4;
        node2.left = node1;
        return node5;
    }

    public static TreeNode LC112createTree() {
        //       -2
        //           -3
        TreeNode node2 = new TreeNode(-2);
        TreeNode node3 = new TreeNode(-3);
        node2.left = null;
        node2.right = node3;
        return node2;
    }

    public static TreeNode LC671createTree() {
        //       2
        //    2       5
        //          5   7
        TreeNode node2 = new TreeNode(2);
        TreeNode node2Left = new TreeNode(2);
        TreeNode node5 = new TreeNode(5);
        TreeNode node5Left = new TreeNode(5);
        TreeNode node7 = new TreeNode(7);
        node2.left = node2Left;
        node2.right = node5;
        node5.left = node5Left;
        node5.right = node7;
        return node2;
    }

    public static TreeNode LC113createTree() {
//                     5
//                    / \
//                   4   8
//                  /   / \
//                 11  13  4
//                /  \    / \
//               7    2  5   1
        TreeNode node5 = new TreeNode(5);
        TreeNode node4 = new TreeNode(4);
        TreeNode node8 = new TreeNode(8);
        TreeNode node11 = new TreeNode(11);
        TreeNode node13 = new TreeNode(13);
        TreeNode node4_1 = new TreeNode(4);
        TreeNode node7 = new TreeNode(7);
        TreeNode node2 = new TreeNode(2);
        TreeNode node5_1 = new TreeNode(5);
        TreeNode node1 = new TreeNode(1);
        node5.left = node4;
        node5.right = node8;
        node4.left = node11;
        node4.right = null;
        node8.left = node13;
        node8.right = node4_1;
        node11.left = node7;
        node11.right = node2;
        node4_1.left = node5_1;
        node4_1.right = node1;
        return node5;
    }

    public static TreeNode LC124createTree() {
        //       -2
        //           -3
        TreeNode node_10 = new TreeNode(-10);
        TreeNode node9 = new TreeNode(9);
        TreeNode node20 = new TreeNode(20);
        TreeNode node15 = new TreeNode(15);
        TreeNode node7 = new TreeNode(7);
        node_10.left = node9;
        node_10.right = node20;

        node20.left = node15;
        node20.right = node7;

        return node_10;
    }

    public static TreeNode LC145createTree() {
        //       -2
        //           -3
        TreeNode node1 = new TreeNode(1);
        TreeNode node2 = new TreeNode(2);
        TreeNode node3 = new TreeNode(3);
        node1.left = null;
        node1.right = node2;

        node2.left = node3;
        return node1;
    }

    public static TreeNode LC572_AMAZON_createTree() {
        TreeNode node2 = new TreeNode(2);
        TreeNode node_2 = new TreeNode(-2);
        TreeNode node14 = new TreeNode(14);
        TreeNode node_1 = new TreeNode(-1);
        TreeNode node1 = new TreeNode(1);
        TreeNode node5 = new TreeNode(5);
        TreeNode node_11 = new TreeNode(-1);
        node2.left = node_2;
        node2.right = node14;
        node_2.left = node_1;
        node_2.right = node1;
        node14.left = node5;
        node14.right = node_11;
        return node2;
    }

    // Input: root = [6,7,8,2,7,1,3,9,null,1,4,null,null,null,5]
    public static Supplier<TreeNode> createTreeLC1302() {
        return getTreeNodeSupplier();
    }

    public static Supplier<TreeNode> createTreeLC1315() {
        return getTreeNodeSupplier();
    }

    public static Supplier<Node> createTreeLC1490() {
        return () -> {
            Node node1 = new Node(1, new ArrayList<>());
            Node node2 = new Node(2, new ArrayList<>());
            Node node3 = new Node(3, new ArrayList<>());
            Node node4 = new Node(4, new ArrayList<>());
            Node node5 = new Node(5, new ArrayList<>());
            Node node6 = new Node(6, new ArrayList<>());

            node1.children.add(node2);
            node1.children.add(node3);
            node1.children.add(node4);

            node3.children.add(node5);
            node3.children.add(node6);

            return node1;
        };
    }

    private static Supplier<TreeNode> getTreeNodeSupplier() {
        return () -> {
            TreeNode node6 = new TreeNode(6);
            TreeNode node7_1 = new TreeNode(7);
            TreeNode node8 = new TreeNode(8);
            TreeNode node2 = new TreeNode(2);
            TreeNode node7_2 = new TreeNode(7);
            TreeNode node1_1 = new TreeNode(1);
            TreeNode node3 = new TreeNode(3);
            TreeNode node9 = new TreeNode(9);
            TreeNode node1_2 = new TreeNode(1);
            TreeNode node4 = new TreeNode(4);
            TreeNode node5 = new TreeNode(5);
            /*
             *         6
             *     7       8
             *    2  7   1  3
             *   9  1,4       5
             * */

            node6.left = node7_1;
            node6.right = node8;

            node7_1.left = node2;
            node7_1.right = node7_2;
            node8.left = node1_1;
            node8.right = node3;

            node2.left = node9;

            node7_2.left = node1_2;
            node7_2.right = node4;

            node3.right = node5;
            return node6;
        };
    }

    public static Supplier<LC133Node> createGraphLC133() {
        return () -> {
            LC133Node node1 = new LC133Node(1);
            LC133Node node2 = new LC133Node(2);
            LC133Node node3 = new LC133Node(3);
            LC133Node node4 = new LC133Node(4);

            node1.neighbors.add(node2);
            node1.neighbors.add(node4);

            node2.neighbors.add(node1);
            node2.neighbors.add(node3);

            node3.neighbors.add(node2);
            node3.neighbors.add(node4);

            node4.neighbors.add(node1);
            node4.neighbors.add(node3);

            return node1;
        };
    }

}
