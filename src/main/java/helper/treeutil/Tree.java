package helper.treeutil;

public interface Tree <T> {
    ObjectNode getRoot();
    void printTree();

    static Tree.Builder builer() {
        return Tree.builer();
    }

    interface Builder<T> {

        Builder setNodes(T[] objects);

        Tree build();
    }
}
