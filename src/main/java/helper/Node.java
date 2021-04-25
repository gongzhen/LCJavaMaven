package helper;

import java.util.List;

public class Node extends AbstractNode {
    public List<Node> children;

    public Node(int val) {
        super(val);
    }

    public Node(int val, List<Node> children) {
        super(val);
        this.children = children;
    }

}


