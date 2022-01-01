package helper.treeutil;

import helper.PrintUtils;
import helper.TreeNode;
import helper.gzlinkedlist.GZLinkedList;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LeetCodeTree<T> {

    private final T[] nodes;
    private final TreeNode root;
    private final boolean isDebug;

    public LeetCodeTree(T[] nodes, TreeNode root) {
        this(nodes, root, false);
    }

    public LeetCodeTree(T[] nodes, TreeNode root, boolean isDebug) {
        this.nodes = nodes;
        this.root = root;
        this.isDebug = isDebug;
    }

    public static LeetCodeTree.BuilderImpl builder() {
        return new LeetCodeTree.BuilderImpl();
    }

    public TreeNode getRoot() {
        return this.root;
    }

    public void printTree() {
        TreeNode node = this.root;
        if (node == null) {
            return;
        }

        GZLinkedList<TreeNode> queue = new GZLinkedList<>();
        queue.offer(node);

        while(!queue.isEmpty()) {
            int len = queue.length();
            for (int i = 0; i < len; i++) {
                TreeNode n = queue.poll();
                PrintUtils.printString("node.value:" + n.val);
                if (n.left != null) {

                    PrintUtils.printString("node.left:" + n.left.val);
                    queue.offer(n.left);
                }
                if (n.right != null) {
                    PrintUtils.printString("node.right:" + n.right.val);
                    queue.offer(n.right);
                }
            }
        }
    }


    public interface Builder<T> {

        LeetCodeTree.Builder setNodes(T[] objects);

        LeetCodeTree.Builder setDebug(boolean isDebug);

        LeetCodeTree build();
    }

    public static final class BuilderImpl<T> implements Builder<T> {
        private @Nullable TreeNode root;
        private @Nullable T[] objects;
        private boolean isDebug;
        private BuilderImpl() {
        }

        @Override
        public Builder setNodes(T[] objects) {
            if (objects.length == 0) {
                return this;
            }
            this.objects = objects.clone();
            if (objects[0] instanceof Integer) {
                Integer[] intObjs = new Integer[objects.length];
                int i = 0;
                for(T o : objects) {
                    intObjs[i++] = (Integer) o;
                }
                buildIntTree(intObjs);
            }
            return this;
        }

        @Override
        public LeetCodeTree.Builder setDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        private void buildIntTree(Integer[] array) {
            this.root = new TreeNode(array[0]);
            TreeNode ptr = this.root;

            GZLinkedList<TreeNode> queue = new GZLinkedList<>();
            queue.offer(ptr);
            int idx = 0;
            while(!queue.isEmpty()) {
                int len = queue.length();
                for (int i = 0; i < len; i++) {
                    TreeNode node = queue.poll();
                    if (this.isDebug == true) {
                        PrintUtils.printString("node:" + node.val);
                    }

                    int leftIdx = idx + 1;
                    if (leftIdx < array.length) {
                        if (array[leftIdx] != null) {
                            if (this.isDebug == true) {
                                PrintUtils.printString("left:" + array[leftIdx]);
                            }

                            node.left = new TreeNode(array[leftIdx]);
                            queue.offer(node.left);
                        } else {
                            node.right = null;
                        }
                    }
                    int rightIdx = idx + 2;
                    if (rightIdx < array.length) {
                        if (array[rightIdx] != null) {
                            if (this.isDebug == true) {
                                PrintUtils.printString("right:" + array[rightIdx]);
                            }
                            node.right = new TreeNode(array[rightIdx]);
                            queue.offer(node.right);
                        } else {
                            node.right = null;
                        }
                    }
                    idx = idx + 2; // to next index.
                }
            }
        }

        @Override
        public LeetCodeTree build() {
            return new LeetCodeTree(this.objects, this.root, this.isDebug);
        }
    }
}
