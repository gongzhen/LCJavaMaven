package leetcode.tree;

import helper.TreeNode;
import helper.TreeUtils;
import helper.TriFunction;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LC1038 {

    public TreeNode bstToGst(TreeNode root) {
        dfs(root, 0);
        return root;
    }

    private int dfs(TreeNode node, int sum) {
        if (node == null) {
            return 0;
        }

        int current = node.val + dfs(node.right, sum); // get the current node and right tree sum for current.
        node.val = current + sum; // update the node.val with current sum and parent sum.
        current += dfs(node.left, node.val); // move to left tree.
        return current;
    }

    private TreeNode bfs(TreeNode root) {
        if (root == null) {
            return null;
        }

        int sum = 0;
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode node = root;

        while (!stack.isEmpty() || node != null) {
            while(node != null) {
                stack.push(node);
                node = node.right;
            }

            node = stack.pop();
            sum += node.val;
            node.val = sum;
            node = node.left;
        }

        return root;
    }

    public static void main(String[] args) {
        Supplier<LC1038> supplier = () -> new LC1038();
        supplier.get().bstToGst(TreeUtils.createTreeLC1315().get());

        TriFunction<Supplier<LC1038>, Supplier<TreeNode>, Boolean, TreeNode> triFunction = (lc1038, rootSupplier, isDFS) -> {
            if (isDFS) {
                return lc1038.get().bstToGst(rootSupplier.get());
            } else {
                return lc1038.get().bfs(rootSupplier.get());
            }
        };

        triFunction.apply(supplier, TreeUtils.createTreeLC1315(), true);
        triFunction.apply(supplier, TreeUtils.createTreeLC1315(), false);
    }
}
