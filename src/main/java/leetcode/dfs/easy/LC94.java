package leetcode.dfs.easy;

import helper.TreeNode;
import helper.gzstack.GZStack;
import helper.treeutil.LeetCodeTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LC94 {

    public List<Integer> inorderTraversal(TreeNode root) {
        // bfs
        if (root == null) {
            return Collections.EMPTY_LIST;
        }
        GZStack<TreeNode> stack = new GZStack<>();
        TreeNode node = root;
        List<Integer> res = new ArrayList<>();
        while(node != null || stack.isEmpty() == false) {
            while(node != null) {
                stack.push(node);
                node = node.left;
            }
            node = stack.pop();
            res.add(node.val);
            node = node.right;
        }
        return res;
    }

    public static void main(String[] args) {
        LeetCodeTree<TreeNode> tree = LeetCodeTree.builder().setNodes(new Integer[]{1, null, 2, 3}).build();
        LC94 lc94 = new LC94();
        lc94.inorderTraversal(tree.getRoot());
    }
}
