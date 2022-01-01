package leetcode.dfs.easy;

import helper.TreeNode;
import javafx.beans.NamedArg;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class LC1740 {

    /**
     * Description
     * Given the root of a binary tree and two integers p and q, return the distance between the
     * nodes of value p and value q in the tree.
     *
     * The distance between two nodes is the number of edges on the path from one to the other.
     *
     * Input: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 0
     * Output: 3
     * Explanation: There are 3 edges between 5 and 0: 5-3-1-0.
     *
     * Input: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 7
     * Output: 2
     * Explanation: There are 2 edges between 5 and 7: 5-2-7.
     *
     * Input: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 5
     * Output: 0
     * Explanation: The distance between a node and itself is 0.
     *
     * 
     */

    private static class LC1740Pair<K, V> implements Serializable {
        private K key;
        private V value;

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.getValue();
        }

        public LC1740Pair(@NamedArg("key") K key, @NamedArg("value")  V value) {
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + (this.key != null ? this.key.hashCode() : 0);
            hash = 31 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (!(o instanceof Pair)) {
                return false;
            } else {
                Pair pair = (Pair)o;
                if (this.key != null) {
                    if (!this.key.equals(pair.getKey())) {
                        return false;
                    }
                } else if (pair.getKey() != null) {
                    return false;
                }

                if (this.value != null) {
                    if (!this.value.equals(pair.getValue())) {
                        return false;
                    }
                } else if (pair.getValue() != null) {
                    return false;
                }

                return false;
            }
        }
    }

    public int findDistance(TreeNode root, int p, int q) {
        if (p == q) {
            return 0;
        }

        Pair<TreeNode, Integer> pPair = searchNode(root, p, 0);
        Pair<TreeNode, Integer> qPair = searchNode(root, q, 0);
        boolean isSameSide = isSameBranch(root, pPair.getKey(), qPair.getKey());

        if (isSameSide) {
            return Math.abs(pPair.getValue() - qPair.getValue());
        } else {
            return pPair.getValue() + qPair.getValue();
        }
    }

    private boolean isSameBranch(TreeNode root, TreeNode pNode, TreeNode qNode) {
        if (root == null || pNode == null || qNode == null) {
            return false;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        int count = 0;
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for(int i = 0; i < size; i++) {
                TreeNode top = queue.poll();
                if (top == pNode) {
                    count++;
                }
                if (top == qNode) {
                    count++;
                }

                if (pNode.left != null) {
                    queue.offer(pNode.left);
                }

                if (pNode.right != null) {
                    queue.offer(pNode.right);
                }
            }
        }

        if (count == 0 || count == 2) {
            return true;
        } else {
            return false;
        }
    }

    private void searchLeftBranch(TreeNode root, TreeNode p) {

    }

    private Pair<TreeNode, Integer> searchNode(TreeNode node, int p, int distant) {
        if (node == null) {
            return null;
        }
        if (node.val == p) {
            return new Pair<>(node, distant);
        }
        Pair<TreeNode, Integer> left = searchNode(node.left, p,  + 1);
        Pair<TreeNode, Integer> right = searchNode(node.right, p, distant + 1);
        if (left.getKey() == null) {
            return right;
        } else {
            return left;
        }
    }

    /**
     * find the lowestCommonAncestor.
     */

    public int findDistance2(TreeNode root, int p, int q) {
        TreeNode commonAncestor = lowestCommonAncestor(root, p, q);
        return getDistance(commonAncestor, p, 0) + getDistance(commonAncestor, q, 0);
    }

    private int getDistance(TreeNode node, int p, int distance) {
        if (node == null) {
            return -1;
        }
        if (node.val == p) {
            return distance;
        }

        int left = getDistance(node.left, p, distance + 1);
        int right = getDistance(node.right, p, distance + 1);
        if (left == -1) {
            return right;
        }
        return left;
    }

    private TreeNode lowestCommonAncestor(TreeNode root, int p, int q) {
        if (root == null) {
            return null;
        }

        if (root.val == p) {
            return root;
        } else if (root.val == q) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left != null && right != null) {
            return root;
        } else if (left != null) {
            return left;
        }
        return right;
    }



    public static void main(String[] args) {

    }
}
