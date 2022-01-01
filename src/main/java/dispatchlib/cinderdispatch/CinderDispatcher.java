package dispatchlib.cinderdispatch;

import java.util.concurrent.CompletableFuture;

public interface CinderDispatcher<DISPATCH_RESULT> {

    CompletableFuture<DISPATCH_RESULT> dispatch();

    public interface Listener<TASK_INPUT, TASK_RESULT> {
        void disPatch(TASK_INPUT var1);

        void onCompletion(TASK_INPUT var1, TASK_INPUT var2);

        void onFailure(TASK_INPUT var1, Throwable var2);

        void onTimeout(TASK_INPUT var1);
    }

    @FunctionalInterface
    public interface InterruptableWorker<TASK_INPUT, TASK_RESULT> {
        TASK_RESULT execute(TASK_INPUT var1) throws InterruptedException;
    }

    @FunctionalInterface
    public interface Worker<TASK_INPUT, TASK_RESULT> {
        CompletableFuture<TASK_RESULT> execute(TASK_INPUT var1);
    }
}
