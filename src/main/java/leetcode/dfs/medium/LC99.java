package leetcode.dfs.medium;

import helper.PrintUtils;
import helper.TreeNode;
import helper.treeutil.LeetCodeTree;

public class LC99 {

    private TreeNode first, second, previous;

    public void recoverTree(TreeNode root) {
        inorder(root);

        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    private void inorder(TreeNode node) {
        if (node == null) {
            return;
        }

        inorder(node.left);

        if (previous != null && previous.val > node.val) {
            if (first == null) {
                first = previous;
            }

            if (first != null) {
                second = node;
            }
        }

        previous = node;
        if (node.right != null) {
            inorder(node.right);
        }
    }

    public static void main(String[] args) {
        LC99 lc99 = new LC99();
        LeetCodeTree tree = LeetCodeTree.builder().setDebug(false).setNodes(new Integer[]{1, 3, null, null, 2}).build();
        tree.printTree();
        PrintUtils.printLine();
        lc99.recoverTree(tree.getRoot());
        tree.printTree();
        PrintUtils.printLine();
        LeetCodeTree tree2 = LeetCodeTree.builder().setDebug(false).setNodes(new Integer[]{3, 1, 4, null, null, 2}).build();
        tree2.printTree();
        PrintUtils.printLine();
        lc99.recoverTree(tree2.getRoot());
        tree2.printTree();
    }
}
