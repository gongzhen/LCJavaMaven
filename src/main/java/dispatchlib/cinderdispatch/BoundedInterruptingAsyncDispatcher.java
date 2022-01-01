package dispatchlib.cinderdispatch;

import java.util.concurrent.CompletableFuture;

public class BoundedInterruptingAsyncDispatcher
        <TASK_INPUT, TASK_RESULT, DISPATCH_RESULT>
        implements CinderDispatcher<DISPATCH_RESULT> {

    private final BoundedInterruptingAsyncDispatcher dispatcher;

    BoundedInterruptingAsyncDispatcher() {
        dispatcher = new BoundedInterruptingAsyncDispatcher();
    }

    @Override
    public CompletableFuture<DISPATCH_RESULT> dispatch() {
        return null;
    }
}
