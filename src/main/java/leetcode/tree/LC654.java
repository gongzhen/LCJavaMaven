package leetcode.tree;

import com.sun.source.tree.Tree;
import helper.TreeNode;

import java.util.Deque;
import java.util.LinkedList;

public class LC654 {

    public TreeNode constructMaximumBinaryTree(int[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }

        return dfs(nums, 0, nums.length - 1);
    }

    public TreeNode bfs(int[] nums) {
        Deque<TreeNode> stack = new LinkedList<>();
        for (int n : nums) {
            TreeNode node = new TreeNode(n);
            while (!stack.isEmpty() && stack.peek().val < n) {
                node.left = stack.pop();
            }
            if (!stack.isEmpty()) {
                stack.peek().right = node;
            }
            stack.push(node);
        }
        return !stack.isEmpty() ? stack.peek() : null;
    }


    private TreeNode dfs(int[] nums, int start, int end) {
        if (nums == null || nums.length == 0 || start > end) {
            return null;
        }

        if (start == end) {
            return new TreeNode(nums[start]);
        }

        int idx = findLargest(nums, start, end);

        TreeNode node = new TreeNode(nums[idx]);

        node.left = dfs(nums, start, idx - 1);
        node.right = dfs(nums, idx + 1, end);
        return node;
    }

    private int findLargest(int[] nums, int start, int end) {
        int largest = nums[start];
        int index = start;
        for(int i = start; i <= end; i++) {
            if (largest <= nums[i]) {
                index = i;
                largest = nums[i];
            }
        }
        return index;
    }

    public static void main(String[] args) {

    }
}
