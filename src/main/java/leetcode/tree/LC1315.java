package leetcode.tree;

import com.sun.source.tree.Tree;
import helper.PrintUtils;
import helper.TreeNode;
import helper.TreeUtils;

import java.util.function.Supplier;

public class LC1315 {

    public int sumEvenGrandparent(TreeNode root) {
        return dfs(root, null, null);
    }

    private int dfs(TreeNode node ,TreeNode parent, TreeNode grandParent) {
        if (node == null) {
            return 0;
        }

        int addition = grandParent != null ? node.val : 0;
        TreeNode _parent = node.val % 2 == 0 ? node : null;
        return addition + dfs(node.left, _parent, parent) + dfs(node.right, _parent, parent);
    }

    public static void main(String[] args) {
        Supplier<LC1315> supplier = () -> {
            return new LC1315();
        };

        int res = supplier.get().sumEvenGrandparent(TreeUtils.createTreeLC1315().get());
        PrintUtils.printString("res: " + res);
    }
}
