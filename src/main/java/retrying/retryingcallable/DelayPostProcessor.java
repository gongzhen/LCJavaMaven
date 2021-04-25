package retrying.retryingcallable;

import javax.annotation.concurrent.ThreadSafe;

/**
 * An interface used to compute or adjust the value of the delay for {@link RetryPolicy#nextDelayMillis(Date, long)}.
 * This interface is really a {@link Function} for long to long.
 * That said, this interface was created independent of {@link Function} because it:
 * - works with primitives without boxing/unboxing.
 * - is more clear about its purpose then the very generic {@link Function} interface.
 */
@ThreadSafe
public interface DelayPostProcessor {

    /**
     * @param delayMillis the value of the computed delay.
     * @return the number of millis to sleep based on current strategy and input delay.
     */
    long getDelayMillisFor(long delayMillis);
}
