package retrying.retryingcallable;

import com.google.common.base.Throwables;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.concurrent.Callable;

import helper.PrintUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class RetryingCallable<T> implements Callable<T> {

//    private final static Logger LOG = LoggerFactory.getLogger(RetryingCallable.class);
    private final static Listener NOOP_LISTENER = new ListenerNullImpl();

    @Nonnull
    private final Callable<T> callable;

    @Nonnull
    private final RetryPolicy retryPolicy;

    @Nonnull
    private Listener listener = NOOP_LISTENER;

    /**
     * @param callable the {@link Callable} to be called.
     * @param retryPolicy the {@link RetryPolicy} to be used.
     */
    public RetryingCallable(@Nonnull Callable<T> callable, @Nonnull RetryPolicy retryPolicy) {
        this.callable = checkNotNull(callable, "callable is null");
        this.retryPolicy = checkNotNull(retryPolicy, "retryPolicy is null");
    }

    /**
     * Adds a hook to this {@link RetryingCallable} for call-backs during execution of
     * {@link RetryingCallable#call()} so that the calling library can log events, capture metrics,
     * etc. If this method is NOT called, a NOOP implementation will be used by default.
     *
     * @param listener a {@link Listener} which will be used to emit call-backs during execution of
     * the {@link RetryingCallable#call()} method.
     * @return
     */
    public RetryingCallable<T> withListener(Listener listener) {
        checkArgument(listener != null,"non-null Listener must be supplied");
        this.listener = listener;
        return this;
    }

    /**
     * Static factory for type inference.
     * @param callable
     * @param retryPolicy
     * @return {@link #RetryingCallable(Callable, RetryPolicy)}.
     */
    public static <T> RetryingCallable<T> newRetryingCallable(@Nonnull Callable<T> callable, @Nonnull RetryPolicy retryPolicy) {
        return new RetryingCallable<>(callable, retryPolicy);
    }

    /**
     * @return computed result from delegate {@link #callable}, employing the {@link RetryPolicy} if necessary.
     * @throws Exception if unable to compute a result before the {@link RetryPolicy} "expires".
     */
    @Override
    public T call() throws Exception {
        int runCount = 0;
        Date startInstant = new Date();
        try {
            listener.onBegin(startInstant);
            while (true) {
                try {
                    listener.onAttempt(runCount + 1);
                    T value = callable.call();
                    listener.onSuccess(runCount + 1, value);
                    return value;
                } catch (Throwable e) {
                    listener.onFailure(runCount + 1, e);
                    handleThrowable(startInstant, ++runCount, e);
                }
            }
        } finally {
            listener.onEnd(startInstant,new Date());
        }
    }

    /**
     * Handle the provided {@link Throwable}.RetryPolicy
     * If the {@link Throwable} could not be recovered from, this method will throw it.
     *
     * @param startInstant See {@link RetryPolicy#nextDelayMillis(Date, long)}
     * @param runCount the run-count.
     * @param t the {@link Throwable} to handle.
     * @throws Exception if {@link RetryPolicy} decides that operation shouldn't be run.
     */
    protected void handleThrowable(Date startInstant, int runCount, Throwable t) throws Exception {
        if (retryPolicy.isFailureRecoverable(t)) {
            long delay = retryPolicy.nextDelayMillis(startInstant, runCount);
            if (delay < 0) {
                throw propagate(t);
            }
            logTransientThrowable(t, runCount, delay);
            if (sleep(delay)) {
                throw propagate(t);
            }
        } else {
            throw propagate(t);
        }
    }

    /**
     * Logs details about the {@link Throwable} to retry.
     * @param t
     * @param runCount
     * @param delayMillis
     */
    protected void logTransientThrowable(Throwable t, int runCount, long delayMillis) {
//        LOG.info(
//                "Transient throwable caught when calling {} after {} attempts.  Will retry again in {} milliseconds.",
//                new Object[] {callable, runCount, delayMillis, t}
//        );
    }

    /**
     * @param t
     * @return null as it always throws.
     * @throws Exception the provided {@link Throwable} is always thrown.
     */
    private Exception propagate(Throwable t) throws Exception {
        // This will always throw t, as t is an Exception or Error.
        Throwables.propagateIfPossible(t, Exception.class);
        return null;
    }

    /**
     * {@link Thread#sleep(long)}s for the specified duration.
     *
     * @param millis milliseconds to {@link Thread#sleep(long)}.
     * @return true if was interrupted, false otherwise.
     * If interruption occurred during sleep, the thread interrupted flag will also be set.
     */
    private boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }

    /**
     * Listener interface for use to capture events during vairous phases of the execution of
     * {@link RetryingCallable#call()} and can be used to log calls, capture metrics, etc - in order
     * to better understand the retry behavior of this callable.
     *
     * Implementations of this interface MUST catch any exceptions within each method
     * implementation and if not, the behavior of the surrounding {@link RetryingCallable} is
     * undefined.
     *
     * @param <T> The return type of the target callable.
     */
    public interface Listener<T> {
        /**
         * This method is called once and only once when {@link RetryingCallable#call()} has just
         * started executing and before any delegate calls are made.
         * @param startInstant the start time
         */
        default void onBegin(Date startInstant) {
            PrintUtils.printString("startInstant:" + startInstant.toString());
        }

        /**
         * This method is called before each delegate {@link Callable#call()} attempt is made.
         * @param attemptIndex the number of the current attempt, starting with 1.
         */
        default void onAttempt(int attemptIndex) {
            PrintUtils.printString("attemptIndex:" + attemptIndex);
        }

        /**
         * This method is called after the delegate {@link Callable#call()} attempt is made, but
         * immediately before the return value is returned.
         * @param attemptIndex the number of the current attempt, starting with 1.
         * @param returnValue the value which will be returned by the successful call to
         * {@link Callable#call()}
         */
        default void onSuccess(int attemptIndex, T returnValue) {
            PrintUtils.printString("attemptIndex:" + attemptIndex + ", returnValue" + returnValue);
        }

        /**
         * This method is called after each delegate {@link Callable#call()} attempt is made when
         * that call throws an exception.
         * @param attemptIndex the number of the current attempt, starting with 1.
         * @param reason The reason the call to {@link Callable#call()} failed.
         */
        default void onFailure(int attemptIndex, Throwable reason) {
            PrintUtils.printString("attemptIndex:" + attemptIndex + ", reason" + reason.getMessage());
        }

        /**
         * This method is called once and only once after all delegate {@link Callable#call()}
         * attempts are made -- when thte call succeeds or when all retries have been exhausted.
         * @param startInstant the start time, which was supplied to {@link Listener#onBegin(Date)}
         * @param endInstant the end time
         */
        default void onEnd(Date startInstant, Date endInstant) {
            PrintUtils.printString("attemptIndex:" + startInstant.toString() + ", endInstant" + endInstant.toString());
        }
    }

    public static final class ListenerNullImpl implements Listener<Object> {

        public void onBegin(Date startInstant) {
            PrintUtils.printString("startInstant:" + startInstant.toString());
        }

        /**
         * This method is called before each delegate {@link Callable#call()} attempt is made.
         * @param attemptIndex the number of the current attempt, starting with 1.
         */
        public void onAttempt(int attemptIndex) {
            PrintUtils.printString("attemptIndex:" + attemptIndex);
        }

        /**
         * This method is called after the delegate {@link Callable#call()} attempt is made, but
         * immediately before the return value is returned.
         * @param attemptIndex the number of the current attempt, starting with 1.
         * @param returnValue the value which will be returned by the successful call to
         * {@link Callable#call()}
         */
        public void onSuccess(int attemptIndex, Object returnValue) {
            PrintUtils.printString("attemptIndex:" + attemptIndex + ", returnValue" + returnValue);
        }

        /**
         * This method is called after each delegate {@link Callable#call()} attempt is made when
         * that call throws an exception.
         * @param attemptIndex the number of the current attempt, starting with 1.
         * @param reason The reason the call to {@link Callable#call()} failed.
         */
        public void onFailure(int attemptIndex, Throwable reason) {
            PrintUtils.printString("attemptIndex:" + attemptIndex + ", reason" + reason.getMessage());
        }

        /**
         * This method is called once and only once after all delegate {@link Callable#call()}
         * attempts are made -- when thte call succeeds or when all retries have been exhausted.
         * @param startInstant the start time, which was supplied to {@link Listener#onBegin(Date)}
         * @param endInstant the end time
         */
        public void onEnd(Date startInstant, Date endInstant) {
            PrintUtils.printString("attemptIndex:" + startInstant.toString() + ", endInstant" + endInstant.toString());
        }

    }
}
