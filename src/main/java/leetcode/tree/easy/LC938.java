package leetcode.tree.easy;

import helper.TreeNode;

public class LC938 {

    public int rangeSumBST(TreeNode root, int low, int high) {
        return dfs(root, low, high);
    }

    private int dfs(TreeNode node, int low, int high) {
        if (node == null) {
            return 0;
        }

        int sum = (node.val >= low && node.val <= high) ? node.val : 0;
        return sum + dfs(node.left, low, high) + dfs(node.right, low, high);
    }

    public static void main(String[] args) {

    }
}
