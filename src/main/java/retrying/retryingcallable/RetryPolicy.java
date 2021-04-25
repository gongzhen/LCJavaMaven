package retrying.retryingcallable;

import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

@ThreadSafe
public interface RetryPolicy {

    /**
     * Called after computation throws a {@link Throwable}.
     *
     * @param t the {@link Throwable} that was thrown.
     * @return true if a retry should be done; false otherwise.
     */
    boolean isFailureRecoverable(Throwable t);

    /**
     * Use this method to compute how much time should sleep until trying another call. Usually,
     * this method should be called after {@link #isFailureRecoverable(Throwable)} returns true.
     *
     * @param startInstant start time of the first run. This is called "startInstant" instead of
     *                     "startDate" because: - If were using JodaTime, this would be a
     *                     ReadableInstant. - JodaTime's terminology is more precise/clear than the
     *                     JDK's.
     * @param numAttempts  the number of previous calls for the method being retried. This is
     *                     expected to be >= 1 as retry logic shouldn't be invoked until at least
     *                     one attempt.
     * @return returns next delay in millis, or negative if no more retries.
     */
    long nextDelayMillis(Date startInstant, int numAttempts);

    /**
     * Interface for use when an API should have a different {@link RetryPolicy} per method.
     */
    interface ForInvocation {
        /**
         * This API matches {@link java.lang.reflect.InvocationHandler}, which is used to
         * proxy/decorate API calls. This method can be used to override the default retry policy
         * based on dynamic information supplied at runtime -- or statically by method signature.
         *
         * @param proxy  The object being called at runtime (see: {@link java.lang.reflect.InvocationHandler})
         * @param method The method being called at runtime (see: {@link java.lang.reflect.InvocationHandler})
         * @param args   The args supplied at runtime (see: {@link java.lang.reflect.InvocationHandler})
         * @return The policy for use with the given API, or {@link Optional#empty()}
         */
        Optional<RetryPolicy> getRetryPolicyForInvocation(Object proxy, final Method method, final Object[] args);
    }

    final class ByMethod implements ForInvocation {
        final private Predicate<Method> methodPredicate;
        final private RetryPolicy retryPolicy;

        public ByMethod(Predicate<Method> methodPredicate, RetryPolicy retryPolicy) {
            if (methodPredicate == null) { throw new IllegalArgumentException("methodPredicate must be supplied"); }
            this.methodPredicate = methodPredicate;
            if (retryPolicy == null) { throw new IllegalArgumentException("retryPolicy must be supplied"); }
            this.retryPolicy = retryPolicy;
        }

        @Override
        public Optional<RetryPolicy> getRetryPolicyForInvocation(Object proxy, Method method, Object[]args){
            return methodPredicate.test(method)
                    ? Optional.of(retryPolicy)
                    : Optional.empty();
        }
    }
}
