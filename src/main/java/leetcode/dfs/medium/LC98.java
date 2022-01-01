package leetcode.dfs.medium;

import helper.PrintUtils;
import helper.TreeNode;
import helper.gzstack.GZStack;
import helper.treeutil.LeetCodeTree;

public class LC98 {

    public boolean isValidBST(TreeNode root) {
        return dfs(root, null, null);
    }

    private boolean dfs(TreeNode root, TreeNode low, TreeNode high) {
        if (root == null) {
            return true;
        }

        if ((low != null && low.val >= root.val) ||
                (high != null && high.val <= root.val)) {
            return false;
        }

        return dfs(root.left, low, root) && dfs(root.right, root, high);
    }

    private boolean bfs(TreeNode root) {
        if (root == null) {
            return true;
        }

        GZStack<TreeNode> stack = new GZStack<>();
        TreeNode node = root;
        TreeNode low = null;
        while (node != null || stack.isEmpty() == false) {
            while(node != null) {
                stack.push(node);
                node = node.left;
            }

            node = stack.pop();
            if (low != null && low.val >= node.val) {
                return false;
            }
            low = node;
            node = node.right;
        }
        return true;
    }

    public static void main(String[] args) {
        TreeNode root = LeetCodeTree.builder().setNodes(new Integer[]{5, 1, 4, null, null, 3, 6}).build().getRoot();

        LC98 lc98 = new LC98();
        PrintUtils.printBool(lc98.isValidBST(root));
        PrintUtils.printBool(lc98.bfs(root));
    }
}
