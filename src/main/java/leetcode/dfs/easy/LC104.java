package leetcode.dfs.easy;

import helper.TreeNode;
import helper.gzstack.GZStack;

public class LC104 {

    public int maxDepth(TreeNode root) {
        return bfs(root);
    }


    /**
     * Create two stack, one for TreeNode, another for depth.
     * Create a max variable to track max depth.
     * 1: push node to stack, and push 1 to depth stack.
     * 2: in while loop, pop out node and depth from stacks.
     * 3: if node's left and right are null, then compare max with depth.
     * 4: if node'left is not null, then push node.left and d+1 to stacks.
     * 5: do the same action to node's right.
     * 6: back to while loop and keep this loop until stack is empty.
     */
    private int bfs(TreeNode node) {
        if (node == null) {
            return 0;
        }

        GZStack<TreeNode> stack = new GZStack<>();
        GZStack<Integer> depth = new GZStack<>();
        TreeNode ptr = node;
        stack.push(ptr);
        depth.push(1);

        int max = 1;

        while(!stack.isEmpty()) {
            ptr = stack.pop();
            int d = depth.pop();
            if (ptr.left == null && ptr.right == null) {
                max = Math.max(d, max);
            }

            if (ptr.left != null) {
                stack.push(ptr.left);
                depth.push(d + 1);
            }

            if (ptr.right != null) {
                stack.push(ptr.right);
                depth.push(d + 1);
            }
        }
        return max;
    }

    public static void main(String[] args) {

    }
}
