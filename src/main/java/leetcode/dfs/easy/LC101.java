package leetcode.dfs.easy;

import helper.TreeNode;
import helper.gzlinkedlist.GZLinkedList;
import helper.gzqueue.GZQueue;

public class LC101 {

    public boolean isSymmetric(TreeNode root) {
        return root == null || dfs(root.left, root.right);
    }

    private boolean dfs(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }

        if (left.val == right.val) {
            return dfs(left.left, right.right) && dfs(left.right, right.left);
        }
        return false;
    }

    private boolean bfs(TreeNode root) {
        if (root == null) {
            return true;
        }
        GZLinkedList<TreeNode> queue = new GZLinkedList<>();
        queue.offer(root.left);
        queue.offer(root.right);
        while(!queue.isEmpty()) {
            TreeNode right = queue.poll();
            TreeNode left = queue.poll();
            if (left == null && right == null) {
                continue;
            }
//            if (left == null || right == null || left.val != right.val) {
//                return false;
//            }
            // ^ bidwise operator. return true if different, return false if same.
            if (left == null ^ right == null) {
                return false;
            }
            if (left.val != right.val) {
                return false;
            }
            queue.offer(left.left);
            queue.offer(right.right);

            queue.offer(left.right);
            queue.offer(right.left);
        }

        return true;
    }

    public static void main(String[] args) {

    }
}
