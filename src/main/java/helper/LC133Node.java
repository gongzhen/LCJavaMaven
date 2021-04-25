package helper;

import java.util.ArrayList;
import java.util.List;

public class LC133Node extends AbstractNode {
    public List<LC133Node> neighbors;

    public LC133Node(int val) {
        super(val);
        this.neighbors = new ArrayList<>();
    }

    public LC133Node(int val, List<LC133Node> neighbors) {
        super(val);
        this.neighbors = neighbors;
    }
}
