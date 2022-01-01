package sequentialdispatcher;

import com.google.inject.internal.util.ImmutableList;
import com.google.inject.internal.util.UnmodifiableIterator;
import helper.GZPreconditions;
import helper.PrintUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class SequentialDispatcher<TASK_INPUT, TASK_RESULT, DISPATCH_RESULT> implements Dispatcher<DISPATCH_RESULT> {

    private final Listener<TASK_INPUT, TASK_RESULT> listener_;
    private final LongSupplier remainingMillisendsSupplier_;
    private final Supplier<DISPATCH_RESULT> resultSupplier_;
    private final ImmutableList<TASK_INPUT> tasks_;
    private final Worker<TASK_INPUT, TASK_RESULT> worker_;

    public SequentialDispatcher(List<TASK_INPUT> tasks,
                                InterruptableWorker<TASK_INPUT, TASK_RESULT> worker,
                                Supplier<DISPATCH_RESULT> resultSupplier,
                                LongSupplier remainingMillisendsSupplier,
                                Listener<TASK_INPUT, TASK_RESULT> listener) {
        this(tasks, wrap(worker), resultSupplier, remainingMillisendsSupplier, listener);
    }

    public SequentialDispatcher(List<TASK_INPUT> tasks,
                                Worker<TASK_INPUT, TASK_RESULT> worker,
                                Supplier<DISPATCH_RESULT> resultSupplier,
                                LongSupplier remainingMillisendsSupplier,
                                Listener<TASK_INPUT, TASK_RESULT> listener) {
        this.tasks_ = ImmutableList.copyOf(tasks);
        this.worker_ = GZPreconditions.checkNotNull(worker, "Worker can't be null");
        this.resultSupplier_ = GZPreconditions.checkNotNull(resultSupplier, "Resultsupplier can't be null");
        this.remainingMillisendsSupplier_ = GZPreconditions.checkNotNull(remainingMillisendsSupplier, "remaining Millisends supplier can't be null");
        this.listener_ = GZPreconditions.checkNotNull(listener, "listener can't be null");
        PrintUtils.printString("SequentialDispatcher");
    }

    @Override
    public CompletableFuture<DISPATCH_RESULT> dispatch() {
        PrintUtils.printString("SequentialDispatcher.dispatch");
        UnmodifiableIterator iterator = this.tasks_.iterator();
        while (iterator.hasNext()) {
            TASK_INPUT input = (TASK_INPUT)iterator.next();
            try {
                PrintUtils.printString("listener_.onDispatch >>>> input:  ", input);
                this.listener_.onDispatch(input);
            } catch (Exception e1) {
                throw e1;
            }

//            LongSupplier remainingMillisendsSupplier,
//            () -> getRemainingMillis(),
            if (remainingMillisendsSupplier_.getAsLong() > 0) {
                boolean failed = false;
                TASK_RESULT result = null;
                try {
//                    Worker<TASK_INPUT, TASK_RESULT> worker_;
//                    @FunctionalInterface
//                    interface Worker<TASK_INPUT, TASK_RESULT> {
//                        CompletableFuture<TASK_RESULT> execute(TASK_INPUT var1);
//                    }
//
//                    (Node document) -> runAdEvaluators(adDocument, adDocuments),
                    PrintUtils.printString("worker_.execute  >>>> input:  ", input);
                    PrintUtils.printString("worker_.execute  >>>> this.worker_:  ", this.worker_);
                    result = this.worker_.execute(input).get();
                    PrintUtils.printString("worker_.execute  >>>> result:  ", result);
                } catch (Exception e2) {
                    failed = true;
                    try {
                        PrintUtils.printString("listener_.onFailure");
                        this.listener_.onFailure(input, e2);
                    } catch (Exception e3) {
                        throw e3;
                    }
                }
                if (!failed) {
                    try {
                        PrintUtils.printString("listener_.onCompletion");
                        this.listener_.onCompletion(input, result);
                    } catch (Exception e4) {
                        throw e4;
                    }
                }
            } else {
                try {
                    PrintUtils.printString("listener_.onTimeoue");
                    this.listener_.onTimeoue(input);
                } catch (Exception e5) {
                    throw e5;
                }
            }
        }
        return CompletableFuture.completedFuture(this.resultSupplier_.get());
    }

    private static <TASK_INPUT, TASK_RESULT> Worker<TASK_INPUT, TASK_RESULT> wrap
            (InterruptableWorker<TASK_INPUT, TASK_RESULT> worker) {
        return (input) -> {
            try {
                PrintUtils.printString("Worker wrap >>>> input:  ", input);
                PrintUtils.printString("Worker wrap >>>> worker:  ", worker);
                return CompletableFuture.completedFuture((worker.execute(input)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
