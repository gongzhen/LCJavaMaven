package retrying.retryingcallable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import static retrying.retryingcallable.PredicateUtils.isInstanceFilters;

abstract class AbstractRetryPolicy implements RetryPolicy {


    /**
     * Tests to determine that an exception is immediately unrecoverable.
     */
    @Nonnull
    private final Collection<Predicate<Throwable>> immediatelyUnrecoverableFilters;

    /**
     * Tests for recoverable exceptions
     */
    @Nonnull
    private final Collection<Predicate<Throwable>> recoverableFilters;

    /**
     * Boolean defining whether or not to recursively search nested throwables as well.
     */
    @Nonnull
    private final boolean checkNestedThrowables;

    @Nonnull
    private final DelayPostProcessor delayPostProcessor;

    /**
     * @param immediatelyUnrecoverableThrowables the {@link Throwable}s that can immediately be deemed as unrecoverable.
     * @param recoverableThrowables the {@link Throwable}s that are considered recoverable.
     * @param delayPostProcessor how the actual delay will starting from computed one.
     * @param checkNestedThrowables if true, also checks the {@link Throwable}s that are nested within the root {@link Throwable}.
     */
    protected AbstractRetryPolicy(
            @Nonnull Collection<Class<? extends Throwable>> immediatelyUnrecoverableThrowables,
            @Nonnull Collection<Class<? extends Throwable>> recoverableThrowables,
            @Nonnull DelayPostProcessor delayPostProcessor,
            boolean checkNestedThrowables) {
        this(
                checkNestedThrowables,
                isInstanceFilters(immediatelyUnrecoverableThrowables),
                isInstanceFilters(recoverableThrowables),
                delayPostProcessor);
    }

    /**
     * @param checkNestedThrowables if true, also checks the {@link Throwable}s that are nested within the root {@link Throwable}.
     * @param immediatelyUnrecoverableFilters runtime test predicates which return true if the inspected
     *                                           {@link Throwable} can immediately be deemed as unrecoverable.
     * @param recoverableFilters runtime test predicates which return true if the inspected {@link Throwable} is
     *                              considered recoverable.
     * @param delayPostProcessor how the actual delay will starting from computed one.
     */
    protected AbstractRetryPolicy(
            boolean checkNestedThrowables,
            @Nonnull Collection<Predicate<Throwable>> immediatelyUnrecoverableFilters,
            @Nonnull Collection<Predicate<Throwable>> recoverableFilters,
            @Nonnull DelayPostProcessor delayPostProcessor
    ) {
        this.immediatelyUnrecoverableFilters = ImmutableList.copyOf(immediatelyUnrecoverableFilters);
        this.recoverableFilters = ImmutableList.copyOf(recoverableFilters);
        this.delayPostProcessor = delayPostProcessor;
        this.checkNestedThrowables = checkNestedThrowables;
    }

    /**
     * Constructs a new instance.
     *
     * @param immediatelyUnrecoverableThrowables the {@link Throwable}s that can immediately be deemed as unrecoverable
     * @param recoverableThrowables the {@link Throwable}s that are considered recoverable.
     * @param checkNestedThrowables if true, also checks the {@link Throwable}s that are nested within the root {@link Throwable}.
     */
    protected AbstractRetryPolicy(
            @Nonnull Collection<Class<? extends Throwable>> immediatelyUnrecoverableThrowables,
            @Nonnull Collection<Class<? extends Throwable>> recoverableThrowables,
            boolean checkNestedThrowables
    ) {
        this(
                immediatelyUnrecoverableThrowables,
                recoverableThrowables,
                DelayPostProcessors.createIdentityDelayPostProcessor(),
                checkNestedThrowables
        );
    }

    /**
     * Constructs a new instance.
     *
     * @param immediatelyUnrecoverableFilters the {@link Throwable}s that can immediately be deemed as unrecoverable
     * @param recoverableFilters the {@link Throwable}s that are considered recoverable.
     * @param checkNestedThrowables if true, also checks the {@link Throwable}s that are nested within the root {@link Throwable}.
     */
    protected AbstractRetryPolicy(
            boolean checkNestedThrowables, //NOTE: position in arg list changed due to solve type-erasure / backwards-compatibility problem
            @Nonnull Collection<Predicate<Throwable>> immediatelyUnrecoverableFilters,
            @Nonnull Collection<Predicate<Throwable>> recoverableFilters
    ) {
        this(
                checkNestedThrowables,
                immediatelyUnrecoverableFilters,
                recoverableFilters,
                DelayPostProcessors.createIdentityDelayPostProcessor()
        );
    }

    /**
     * Called after computation throws exception.
     *
     * @param e the exception that was thrown.
     * @return false if exception is same class or a subclass of one of unrecoverable exceptions,
     *         true if exception is same class or a subclass of one of the recoverable-exceptions, false
     *         for the rest all the other exceptions.
     */
    @Override
    public boolean isFailureRecoverable(Throwable e) {
        Collection<Throwable> causeChain;
        if (checkNestedThrowables) {
            // check the whole chain - returns the chain in order so we
            // start with the original throwable and work our way down
            causeChain = Throwables.getCausalChain(e);
        } else {
            // only check the top-level throwable
            causeChain = Arrays.asList(e);
        }

        boolean isRecoverable = false;

        for (Throwable throwable : causeChain) {
            for (Predicate<Throwable> immediatelyUnrecoverableTest : immediatelyUnrecoverableFilters) {
                if (immediatelyUnrecoverableTest.test(throwable)) {
                    return false;
                }
            }
            for (Predicate<Throwable> recoverableTest : recoverableFilters) {
                if (recoverableTest.test(throwable)) {
                    isRecoverable = true;
                }
            }
        }
        return isRecoverable;
    }

    @VisibleForTesting
    boolean getCheckNestedThrowables() {
        return checkNestedThrowables;
    }

    @VisibleForTesting
    @Nonnull
    DelayPostProcessor getDelayPostProcessor() {
        return delayPostProcessor;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(AbstractRetryPolicy.class)
                .add("immediatelyUnrecoverableFilters", immediatelyUnrecoverableFilters)
                .add("recoverableFilters", recoverableFilters)
                .add("delayPostProcessor", delayPostProcessor)
                .toString();
    }
}
