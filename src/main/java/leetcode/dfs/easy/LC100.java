package leetcode.dfs.easy;

import helper.TreeNode;
import helper.gzlinkedlist.GZLinkedList;
import helper.gzstack.GZStack;
import helper.treeutil.Tree;

import java.util.LinkedList;
import java.util.Queue;

public class LC100 {

    public boolean isSameTree(TreeNode p, TreeNode q) {
//        if (p == null && q == null) {
//            return true;
//        }
//        if (p != null) {
//            if (q == null) {
//                return false;
//            }
//        }
//
//        if (q != null) {
//            if (p == null) {
//                return false;
//            }
//        }

        if (p == null && q == null) {
            return true;
        }

        if (p == null || q == null) {
            return false;
        }

        if (p.val != q.val) {
            return false;
        }

        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }

    private boolean bfs(TreeNode p, TreeNode q) {
        GZLinkedList<TreeNode> queue = new GZLinkedList<>();
        queue.offer(p);
        queue.offer(q);

        while(!queue.isEmpty()) {
            TreeNode n1 = queue.poll();
            TreeNode n2 = queue.poll();
            if (n1 == null && n2 == null) {
                continue;
            } else if (n1 == null || n2 == null || n1.val != n2.val) {
                return false;
            }
            queue.offer(n1.left);
            queue.offer(n2.left);
            queue.offer(n1.right);
            queue.offer(n2.right);
        }
        return true;
    }

    public static void main(String[] args) {

    }
}
