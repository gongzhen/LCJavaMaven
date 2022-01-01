package helper.treeutil;

public interface Node {

    Object getValue();

    Node getLeft();

    Node getRight();

    Node setLeft(Object object);

    Node setRight(Object object);
}
