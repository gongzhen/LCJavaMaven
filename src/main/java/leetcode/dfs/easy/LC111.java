package leetcode.dfs.easy;

import helper.TreeNode;
import helper.gzlinkedlist.GZLinkedList;
import helper.gzqueue.GZQueue;

public class LC111 {
    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int h = 1;
        if (root.left == null) {
            h = minDepth(root.right) + 1;
        } else if (root.right == null) {
            h = minDepth(root.left) + 1;
        } else {
            h = 1 + Math.min(minDepth(root.left), minDepth(root.right));
        }
        return h;
    }

    private int dfs1(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int left = dfs1(root.left);
        int right = dfs1(root.right);
        return (left == 0 || right == 0) ? left + right + 1 : Math.min(left, right) + 1;
    }

    private int bfs1(TreeNode root) {
        if (root == null) {
            return 0;
        }

        GZLinkedList<TreeNode> queue = new GZLinkedList<>();
        queue.offer(root);
        int min = 0;
        boolean flag = false;
        while(!queue.isEmpty()) {
            min++;
            int size = queue.length();
            for(int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.offer(node.left);
                }

                if (node.right != null) {
                    queue.offer(node.right);
                }

                if (node.left == null && node.right == null) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        return min;
    }

    public static void main(String[] args) {

    }
}
