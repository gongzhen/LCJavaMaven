package sequentialdispatcher;

import helper.PrintUtils;

import java.util.concurrent.CompletableFuture;

public class FlowScriptManagerMain {

    public static void main(String[] args) {
        FlowScriptManagerServiceImpl flowScriptManagerService = new FlowScriptManagerServiceImpl();
        CompletableFuture<FlowScriptManagerServiceImpl.RequestStatus> request = flowScriptManagerService.executeBidRequest();
        PrintUtils.printString(request.toString());
    }
}
