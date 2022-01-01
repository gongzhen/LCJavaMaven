package leetcode.tree;

import com.google.common.collect.Lists;
import com.sun.source.tree.Tree;
import helper.PrintUtils;
import helper.QueueUtils;
import helper.TreeNode;
import helper.TreeUtils;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

// https://leetcode.jp/problemdetail.php?id=1469
public class LC1469 {
    public List<Integer> getLonelyNodes(TreeNode root) {
        List<Integer> res = Lists.newArrayList();
        dfs(root, res, false);
        return res;
    }

    private void dfs(TreeNode node, List<Integer> list, boolean isLonely) {
        if (node == null) {
            return;
        }
        if (isLonely == true) {
            list.add(node.val);
        }
        dfs(node.left, list, node.right == null);
        dfs(node.right, list, node.left == null);
    }

    private List<Integer> bfs(TreeNode root) {
        if (root == null) {
            return null;
        }

        Queue<TreeNode> queue = QueueUtils.createQueue();
        List<Integer> res = Lists.newArrayList();

        queue.offer(root);

        while(!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (node.left != null) {
                queue.offer(node.left);
                if (node.right == null) {
                    res.add(node.left.val);
                }
            }

            if (node.right != null) {
                queue.offer(node.right);
                if (node.left == null) {
                    res.add(node.right.val);
                }
            }
        }

        return res;
    }

    public static void main(String[] args) {
        Supplier<LC1469> supplier = () -> new LC1469();
        List<Integer> res = supplier.get().getLonelyNodes(TreeUtils.createTreeLC1469().get());
        PrintUtils.printListInteger(res);

        res = supplier.get().bfs(TreeUtils.createTreeLC1469().get());
        PrintUtils.printListInteger(res);
    }
}
