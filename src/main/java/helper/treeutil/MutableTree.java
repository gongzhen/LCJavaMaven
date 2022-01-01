package helper.treeutil;

import helper.PrintUtils;
import helper.gzlinkedlist.GZLinkedList;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MutableTree<T> implements Tree<T> {

    private final T[] nodes;
    private ObjectNode root;

    public MutableTree(T[] nodes, ObjectNode root) {
        this.nodes = nodes;
        this.root = root;
    }

    public static MutableTree.BuilderImpl builder() {
        return new MutableTree.BuilderImpl();
    }

    @Override
    public String toString() {
        return "MutableTree{" + "root=" + root + '}';
    }

    @Override
    public ObjectNode getRoot() {
        return this.root;
    }

    @Override
    public void printTree() {
        ObjectNode node = this.root;
        if (node == null) {
            return;
        }

        GZLinkedList<ObjectNode> queue = new GZLinkedList<>();
        queue.offer(node);

        while(!queue.isEmpty()) {
            int len = queue.length();
            for (int i = 0; i < len; i++) {
                ObjectNode n = queue.poll();
                if (n.getValue() != null) {
                    PrintUtils.printString("node.value:" + n.getValue());
                }
                if (n.getLeft() != null && n.getLeft().getValue() != null) {
                    PrintUtils.printString("node.left:" + n.getLeft().getValue());
                    queue.offer(n.getLeft());
                }
                if (n.getRight() != null && n.getRight().getValue() != null) {
                    PrintUtils.printString("node.right:" + n.getRight().getValue());
                    queue.offer(n.getRight());
                }
            }
        }
    }

    public static final class BuilderImpl<T> implements Tree.Builder<T> {
        private @Nullable ObjectNode root;
        private @Nullable T[] objects;

        private BuilderImpl() {
        }

        @Override
        public MutableTree.BuilderImpl setNodes(T[] objects) {
            this.objects = objects.clone();
            buildTree(objects);
            return this;
        }

        private void buildTree(T[] array) {
            if (array == null || array.length == 0) {
                return;
            }

            this.root = new ObjectNode(array[0]);
            ObjectNode ptr = this.root;

            GZLinkedList<ObjectNode> queue = new GZLinkedList<>();
            queue.offer(ptr);
            int idx = 0;
            while(!queue.isEmpty()) {
                int len = queue.length();
                for (int i = 0; i < len; i++) {
                    ObjectNode node = queue.poll();
                    PrintUtils.printString("node:" + node.toString());
                    int leftIdx = idx + 1;
                    if (leftIdx < array.length) {
                        if (array[leftIdx] != null) {
                            PrintUtils.printString("left:" + array[leftIdx]);
                            node.setLeft(array[leftIdx]);
                            queue.offer(node.getLeft());
                        } else {
                            node.setLeft(null);
                        }
                    }
                    int rightIdx = idx + 2;
                    if (rightIdx < array.length) {
                        if (array[rightIdx] != null) {
                            PrintUtils.printString("right:" + array[rightIdx]);
                            node.setRight(array[rightIdx]);
                            queue.offer(node.getRight());
                        } else {
                            node.setRight(null);
                        }
                    }
                    idx = idx + 2; // to next index.
                }
            }
        }

        @Override
        public MutableTree build() {
            return new MutableTree(this.objects, this.root);
        }
    }
}
