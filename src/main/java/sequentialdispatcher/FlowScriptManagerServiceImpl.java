package sequentialdispatcher;

import com.google.common.collect.ImmutableList;
import helper.Node;
import helper.PrintUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FlowScriptManagerServiceImpl {

    enum RequestStatus {
        OK,
        PUNT,
        ERROR,
        THROTTLE;

        private RequestStatus() {
        }
    }

    FlowScriptManagerServiceImpl() {
    }

    public CompletableFuture<RequestStatus> executeBidRequest() {
        List<Node> adDocuments = new ArrayList<>();
        Node adDocument = new Node(1, null);
        Node node1 = new Node(1, null);
        Node node2 = new Node(1, null);
        adDocuments.addAll(ImmutableList.of(node1, node2));
        Dispatcher<RequestStatus> dispatcher = new SequentialDispatcher<>(
                adDocuments,
                (Node document) -> runAdEvaluators(adDocument, adDocuments),
                () -> selectWinnder(),
                () -> getRemainingMillis(),
                new DispatchListener(adDocument, false));
        return dispatcher.dispatch();
    }

    private boolean runAdEvaluators(Node adDocument,
                                    List<Node> adEvaluators) {
        PrintUtils.printString("runAdEvaluators >>> ", adDocument, adEvaluators);
        return adDocument != null && adEvaluators.size() != 0;
    }

    private RequestStatus selectWinnder() {
        PrintUtils.printString("selectWinnder");
        return RequestStatus.OK;
    }

    private long getRemainingMillis() {
        PrintUtils.printString("getRemainingMillis");
        return 10L;
    }

    private static class DispatchListener implements Dispatcher.Listener<Node, Boolean> {

        private Node node;
        private Boolean isShortCircuted;

        DispatchListener(Node node, Boolean isShortCircuted) {
            this.node = node;
            this.isShortCircuted = isShortCircuted;
        }

        @Override
        public void onDispatch(Node var1) {
            PrintUtils.printString("DispatchListener.onDispatch >>>> var1:  ", var1);
        }

        @Override
        public void onCompletion(Node var1, Boolean var2) {
            PrintUtils.printString("DispatchListener.onCompletion >>>> var1:  ", var1, var2);
        }

        @Override
        public void onFailure(Node var1, Throwable var2) {
            PrintUtils.printString("DispatchListener.onFailure >>>> var1:  ", var1, var2);
        }

        @Override
        public void onTimeoue(Node var1) {
            PrintUtils.printString("DispatchListener.onTimeoue >>>> var1:  ", var1);
        }
    }
}
