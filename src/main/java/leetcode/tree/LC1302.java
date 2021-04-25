package leetcode.tree;

import helper.PrintUtils;
import helper.TreeNode;
import helper.TreeUtils;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

public class LC1302 {

    private static int height;
    private static int sum = 0;

    private int deepestLeavesSumBFS(TreeNode root) {
        Objects.requireNonNull(root);

        Queue<TreeNode> queue = queueSupplier().get();
        queue.offer(root);
        int res = 0;
        int size = 0;
        while (!queue.isEmpty()) {
            size = queue.size();
            for(int i = size - 1; i >= 0; --i) {
                TreeNode node = queue.poll();

                if (node.left == null && node.right == null) {
                    res += node.val;
                }
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer((node.right));
                }
            }
            if (queue.size() != 0) {
                res = 0;
            }
        }
        return res;
    }

    private Supplier<Queue<TreeNode>> queueSupplier() {
        return () -> {
            return new LinkedList<>();
        };
    }

    public int deepestLeavesSum(TreeNode root) {
        if (root == null) {
            return 0;
        }
        height = dfs(root);
        getsum(root, 1);
        return sum;
    }

    private void getsum(TreeNode node, int h) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            if (h == height) {
                sum += node.val;
            }
            return;
        }

        getsum(node.left, h + 1);
        getsum(node.right, h + 1);
    }

    private int dfs(TreeNode node) {
        if (node == null) {
            return 0;
        }

        return 1 + Math.max(dfs(node.left), dfs(node.right));
    }

    public static void main(String[] args) {
        Supplier<LC1302> supplier = () -> {
            return new LC1302();
        };

        int res = supplier.get().deepestLeavesSum(TreeUtils.createTreeLC1302().get());
        PrintUtils.printString("res: " + res);
    }

}
