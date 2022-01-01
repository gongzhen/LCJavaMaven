package helper.treeutil;

public class ObjectNode implements Node {

    private Object object;
    private ObjectNode left;
    private ObjectNode right;

    public ObjectNode() {
        this.object = null;
    }

    public ObjectNode(Object object) {
        this.object = object;
    }

    @Override
    public Object getValue() {
        return this.object;
    }

    @Override
    public ObjectNode getLeft() {
        return this.left;
    }

    @Override
    public ObjectNode getRight() {
        return this.right;
    }

    @Override
    public Node setLeft(Object object) {
        this.left = new ObjectNode(object);
        return this.left;
    }

    @Override
    public Node setRight(Object object) {
        this.right = new ObjectNode(object);
        return this.right;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "object=" + object +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
