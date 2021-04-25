package retrying.retryingcallable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static retrying.retryingcallable.DelayPostProcessors.createIdentityDelayPostProcessor;
import static retrying.retryingcallable.PredicateUtils.isInstanceFilters;
import static retrying.retryingcallable.PredicateUtils.wrapAndMerge;

@ThreadSafe
public class ExponentialBackoffRetryPolicy extends AbstractRetryPolicy {

//    private static final Logger LOG = LoggerFactory.getLogger(ExponentialBackoffRetryPolicy.class);

    private final double backoffCoefficient;

    private final long multiplierMillis;

    private final int maxAttempts;

    private final long maxDelayMillis;

    private final long expirationDurationMillis;

    private final int maxPossibleAttemptsBeforeOverflow;

    /**
     * @param immediatelyUnrecoverableThrowables See {@link AbstractRetryPolicy}.
     * @param recoverableThrowables See {@link AbstractRetryPolicy}.
     * @param backoffCoefficient the backoffCoefficient used to compute the delay.
     * @param multiplierMillis multiplier used to compute the delay.
     * @param maxAttempts maximum number of attempts to retry. {@link #nextDelayMillis(Date, int)} will return a
     * negative number if the <code>numAttempts</code> is >= maxAttempts.
     * @param maxDelayMillis the upper limit for delay in millis.
     * In other words, delay will be equal to <code>min(maxDelay, multiplier * backoffCoefficient^ (n -1))</code>
     * @param expirationDurationMillis how much time from the first call before no longer retrying.
     * Like "maxTriesCount", this provides an upper bound on the number of retries.
     * If startInstant + expirationDurationMillis < now + computedDelay, then {@link #nextDelayMillis(Date, int)} will
     * return -1.
     * @param delayPostProcessor See {@link AbstractRetryPolicy}.
     * @param checkNestedThrowables See {@link AbstractRetryPolicy}.
     */
    public ExponentialBackoffRetryPolicy(
            Collection<Class<? extends Throwable>> immediatelyUnrecoverableThrowables,
            Collection<Class<? extends Throwable>> recoverableThrowables,
            double backoffCoefficient,
            long multiplierMillis,
            int maxAttempts,
            long maxDelayMillis,
            long expirationDurationMillis,
            DelayPostProcessor delayPostProcessor,
            boolean checkNestedThrowables) {
        this(   checkNestedThrowables,
                isInstanceFilters(immediatelyUnrecoverableThrowables),
                isInstanceFilters(recoverableThrowables),
                backoffCoefficient,
                multiplierMillis,
                maxAttempts,
                maxDelayMillis,
                expirationDurationMillis,
                delayPostProcessor);
    }

    /**
     * @param checkNestedThrowables See {@link AbstractRetryPolicy}.
     * @param immediatelyUnrecoverableFilters See {@link AbstractRetryPolicy}.
     * @param recoverableFilters See {@link AbstractRetryPolicy}.
     * @param backoffCoefficient the backoffCoefficient used to compute the delay.
     * @param multiplierMillis multiplier used to compute the delay.
     * @param maxAttempts maximum number of attempts to retry. {@link #nextDelayMillis(Date, int)} will return a
     * negative number if the <code>numAttempts</code> is >= maxAttempts.
     * @param maxDelayMillis the upper limit for delay in millis.
     * In other words, delay will be equal to <code>min(maxDelay, multiplier * backoffCoefficient^ (n -1))</code>
     * @param expirationDurationMillis how much time from the first call before no longer retrying.
     * Like "maxTriesCount", this provides an upper bound on the number of retries.
     * If startInstant + expirationDurationMillis < now + computedDelay, then {@link #nextDelayMillis(Date, int)} will
     * return -1.
     * @param delayPostProcessor See {@link AbstractRetryPolicy}.

     */
    public ExponentialBackoffRetryPolicy(
            boolean checkNestedThrowables,
            Collection<Predicate<Throwable>> immediatelyUnrecoverableFilters,
            Collection<Predicate<Throwable>> recoverableFilters,
            double backoffCoefficient,
            long multiplierMillis,
            int maxAttempts,
            long maxDelayMillis,
            long expirationDurationMillis,
            DelayPostProcessor delayPostProcessor) {
        super(
                checkNestedThrowables,
                immediatelyUnrecoverableFilters,
                recoverableFilters,
                delayPostProcessor);
        this.backoffCoefficient = backoffCoefficient;
        this.multiplierMillis = multiplierMillis;
        this.maxAttempts = maxAttempts;
        this.maxDelayMillis = maxDelayMillis;
        this.expirationDurationMillis = expirationDurationMillis;

        /*
         * We want to know n for the max delay of Long.MAX_VALUE.
         *
         * delay = multiplier * backoffCoefficient^(n-1);
         *
         * delay/multiplier = backoffCoefficient^(n-1)
         *
         * log(delay/multiplier) = log(backoffCoefficient^(n-1))
         *
         * log(delay/multiplier) = (n-1) * log(backoffCoefficient)
         *
         * log(delay/multiplier) / log(backoffCoefficient) = n-1
         *
         * (log(delay/multiplier) / log(backoffCoefficient)) + 1 = n
         */
        // Handle the divide by zero case.
        if (multiplierMillis == 0) {
            maxPossibleAttemptsBeforeOverflow = Integer.MAX_VALUE;
        } else {
            maxPossibleAttemptsBeforeOverflow =
                    (int)(Math.log(Long.MAX_VALUE / multiplierMillis) / Math.log(backoffCoefficient)) + 1;
        }
    }

    /**
     * @param immediatelyUnrecoverableThrowables See {@link AbstractRetryPolicy}.
     * @param recoverableThrowables See {@link AbstractRetryPolicy}.
     * @param backoffCoefficient the backoffCoefficient used to compute the delay.
     * @param multiplierMillis multiplier used to compute the delay.
     * @param maxAttempts maximum number of attempts to retry. {@link #nextDelayMillis(Date, int)} will return a
     * negative number if the <code>numAttempts</code> is >= maxAttempts.
     * @param maxDelayMillis the upper limit for delay in millis.
     * In other words, delay will be equal to <code>min(maxDelay, multiplier * backoffCoefficient^ (n -1))</code>
     * @param expirationDurationMillis how much time from the first call before no longer retrying.
     * Like "maxTriesCount", this provides an upper bound on the number of retries.
     * If startInstant + expirationDurationMillis < now + computedDelay, then {@link #nextDelayMillis(Date, int)} will
     * return -1.
     * @param delayPostProcessor See {@link AbstractRetryPolicy}.
     */
    public ExponentialBackoffRetryPolicy(
            Collection<Class<? extends Throwable>> immediatelyUnrecoverableThrowables,
            Collection<Class<? extends Throwable>> recoverableThrowables,
            double backoffCoefficient,
            long multiplierMillis,
            int maxAttempts,
            long maxDelayMillis,
            long expirationDurationMillis,
            DelayPostProcessor delayPostProcessor) {
        this(
                false,
                isInstanceFilters(immediatelyUnrecoverableThrowables),
                isInstanceFilters(recoverableThrowables),
                backoffCoefficient,
                multiplierMillis,
                maxAttempts,
                maxDelayMillis,
                expirationDurationMillis,
                delayPostProcessor);
    }

    /**
     * @param immediatelyUnrecoverableFilters See {@link AbstractRetryPolicy}.
     * @param recoverableFilters See {@link AbstractRetryPolicy}.
     * @param backoffCoefficient the backoffCoefficient used to compute the delay.
     * @param multiplierMillis multiplier used to compute the delay.
     * @param maxAttempts maximum number of attempts to retry. {@link #nextDelayMillis(Date, int)} will return a
     * negative number if the <code>numAttempts</code> is >= maxAttempts.
     * @param maxDelayMillis the upper limit for delay in millis.
     * In other words, delay will be equal to <code>min(maxDelay, multiplier * backoffCoefficient^ (n -1))</code>
     * @param expirationDurationMillis how much time from the first call before no longer retrying.
     * Like "maxTriesCount", this provides an upper bound on the number of retries.
     * If startInstant + expirationDurationMillis < now + computedDelay, then {@link #nextDelayMillis(Date, int)} will
     * return -1.
     * @param delayPostProcessor See {@link AbstractRetryPolicy}.
     */
    public ExponentialBackoffRetryPolicy(
            Collection<Predicate<Throwable>> immediatelyUnrecoverableFilters,
            Collection<Predicate<Throwable>> recoverableFilters,
            @Nonnull Double backoffCoefficient, //NOTE: changed to Double object to avoid type erasure conflicts while maintaining backwards-compatibility.
            long multiplierMillis,
            int maxAttempts,
            long maxDelayMillis,
            long expirationDurationMillis,
            DelayPostProcessor delayPostProcessor) {
        this(
                false,
                immediatelyUnrecoverableFilters,
                recoverableFilters,
                backoffCoefficient,
                multiplierMillis,
                maxAttempts,
                maxDelayMillis,
                expirationDurationMillis,
                delayPostProcessor);
    }

    /**
     * @inheritDoc
     */
    @Override
    public long nextDelayMillis(Date startInstant, int numAttempts) {
        checkArgument(numAttempts > 0, "Invalid value <{}> for number of attempts.", numAttempts);
        if (numAttempts >= maxAttempts) {
//            LOG.debug("Returning negative nextDelayMillis due to {} attempts out of {}.",
//                    new Object[] {numAttempts, maxAttempts});
            return -1;
        }

        int truncatedNumAttempts = Math.min(maxPossibleAttemptsBeforeOverflow, numAttempts);

        long delayMillis = (long)Math
                .min(maxDelayMillis, multiplierMillis * Math.pow(backoffCoefficient, truncatedNumAttempts - 1));
        delayMillis = this.getDelayPostProcessor().getDelayMillisFor(delayMillis);
        BigInteger expirationTimeMillis = addSafely(startInstant.getTime(), expirationDurationMillis);
        BigInteger nextAttemptTimeMillis = addSafely(new Date().getTime(), delayMillis);
        if (expirationTimeMillis.compareTo(nextAttemptTimeMillis) < 0) {
//            LOG.debug("Returning negative nextDelayMillis since attempt #{} with nextAttemptTimeMillis {} would be "
//                            + "after {}. Total expiration duration is {} ms.",
//                    new Object[] {numAttempts, nextAttemptTimeMillis, expirationTimeMillis, expirationDurationMillis});
            return -1;
        }
        return delayMillis;
    }

    /**
     * @param long1
     * @param long2
     * @return {@link BigInteger} result of adding the provided longs.
     *         {@link BigInteger} is returned to prevent overflow.
     */
    private BigInteger addSafely(long long1, long long2) {
        return BigInteger.valueOf(long1).add(BigInteger.valueOf(long2));
    }

    @VisibleForTesting
    double getBackoffCoefficient() {
        return backoffCoefficient;
    }

    @VisibleForTesting
    long getMultiplierMillis() {
        return multiplierMillis;
    }

    @VisibleForTesting
    int getMaxAttemts() {
        return maxAttempts;
    }

    @VisibleForTesting
    long getMaxDelayMillis() {
        return maxDelayMillis;
    }

    @VisibleForTesting
    long getExpirationDurationMillis() {
        return expirationDurationMillis;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("super", super.toString())
                .add("backoffCoefficient", backoffCoefficient)
                .add("multiplierMillis", multiplierMillis)
                .add("maxAttempts", maxAttempts)
                .add("maxDelayMillis", maxDelayMillis)
                .add("expirationDurationMillis", expirationDurationMillis)
                .toString();
    }

    /**
     * Builder for {@link ExponentialBackoffRetryPolicy}.
     * This class attempts to use reasonable defaults, but these are subject to change.
     * Be sure to review the default values before updating versions.
     * The safest approach is to have one's own {@link Builder} factory,
     * that returns {@link Builder} instances with defaults set to values appropriate for one's own use case.
     *
     * Defaults:
     * - {@link #backoffCoefficient} 1.5
     * - {@link #multiplierMillis} 100
     * - {@link #maxAttempts} 5
     * - {@link #maxDelayMillis} {@link Long#MAX_VALUE}
     * - {@link #expirationDurationMillis} {@link Long#MAX_VALUE}
     * - {@link #delayPostProcessor} {@link DelayPostProcessors#createIdentityDelayPostProcessor()}
     * - {@link #immediatelyUnrecoverableFilters} empty
     * - {@link #recoverableFilters} {@link Any#INSTANCE}
     *
     * Key things to note about these defaults:
     * - Expiration happens based on attempts, not duration.
     * - Exponential backoff occurs, not fixed delay.
     * - No jitter is applied to the delays.  Adding jitter is a suggested best practice for large fleets retrying,
     * but is not the most intuitive default behavior.  This is discussed in https://cr.amazon.com/r/556603/.
     * - All {@link Exception}s are retried; {@link Error}s are not.
     */
    @NotThreadSafe
    public static class Builder {

        protected double backoffCoefficient = 1.5;

        protected long multiplierMillis = 100;

        protected int maxAttempts = 5;

        // Intentionally don't set a lower value for maxDelayMillis
        // so we don't get into the situation of a user setting multiplierMillis to be greater than maxDelayMillis,
        // at which point the delay will get truncated to maxDelayMillis.
        protected long maxDelayMillis = Long.MAX_VALUE;

        // Defaults favor expiration based on attempts instead of duration.
        protected long expirationDurationMillis = Long.MAX_VALUE;

        protected DelayPostProcessor delayPostProcessor = createIdentityDelayPostProcessor();

        protected Collection<Predicate<Throwable>> immediatelyUnrecoverableFilters = Collections.emptyList();

        protected Collection<Predicate<Throwable>> recoverableFilters = Lists.newArrayList(Any.INSTANCE);

        protected boolean checkNestedThrowables = false;

        public ExponentialBackoffRetryPolicy build() {
            return new ExponentialBackoffRetryPolicy(
                    checkNestedThrowables,
                    immediatelyUnrecoverableFilters,
                    recoverableFilters,
                    backoffCoefficient,
                    multiplierMillis,
                    maxAttempts,
                    maxDelayMillis,
                    expirationDurationMillis,
                    delayPostProcessor
            );
        }

        /*
         * NOTE: this holder is done to capture the 'default' values for the Retryable annotation such that
         * they can be retrieved and dynamically compared when applying defaulting.
         *
         * This is to avoid duplication of the 'default' considerations in the code below and thus requiring
         * co-ordinated change across this class if / when Retryable is updated - which could easily be missed.
         */
        @Retryable //Retryable with no properties will instantiate fully with defaults
        private static class RetryableDefaultValues {
            static final Retryable DEFAULTS 		 = RetryableDefaultValues.class.getAnnotation(Retryable.class);
            static final long      DEFAULT_DELAY     = DEFAULTS.delayUnit().toMillis(DEFAULTS.delay());
            static final long      DEFAULT_MAX_DELAY = DEFAULTS.maxDelyUnit().toMillis(DEFAULTS.maxDelay());
            static final long      DEFAULT_EXPIRY    = DEFAULTS.expirationDurationUnit()
                    .toMillis(DEFAULTS.expirationDuration());
            static final Collection<Predicate<Throwable>> DEFAULT_RECOVERABLE_FILTERS = ImmutableList.of(Any.INSTANCE);
            static final Collection<Predicate<Throwable>> DEFAULT_UNRECOVERABLE_FILTERS = isInstanceFilters(DEFAULTS.immediatelyUnrecoverableThrowables());
        }

        /**
         * @param annotation
         * @return a {@link Builder} configured in the ways specified in {@code annotation}. This is
         *         equivalent to calling the following setters with appropriate annotation-backed
         *         values:
         *         <ul>
         *         <li>{@link #withBackoffCoefficient(double)}</li>
         *         <li>{@link #withMaxDelayMillis(long)}</li>
         *         <li>{@link #withMaxAttempts(int)}</li>
         *         <li>{@link #withMultiplierMillis(long)}</li>
         *         <li>{@link #withExpirationDurationMillis(long)}</li>
         *         <li>{@link #withRecoverableThrowables(Class...)}</li>
         *         <li>{@link #withImmediatelyUnrecoverableThrowables(Class...)}</li>
         *         <li>{@link #withCheckNestedThrowables(boolean)}</li>
         *         </ul>
         * @see Retryable
         */
        public Builder withAnnotation(final Retryable annotation) {
            return this.withBackoffCoefficient(annotation.backoffCoefficient())
                    .withJitterScalingCoefficient(annotation.jitterScalingCoefficient())
                    .withMaxDelayMillis(annotation.maxDelyUnit().toMillis(annotation.maxDelay()))
                    .withMaxAttempts(annotation.maxAttempts())
                    .withMultiplierMillis(annotation.delayUnit().toMillis(annotation.delay()))
                    .withExpirationDurationMillis(annotation.expirationDurationUnit()
                            .toMillis(annotation.expirationDuration()))
                    .withRecoverableAnnotationFilters(
                            wrapAndMerge(
                                    annotation.recoverableThrowables(),
                                    (Class<Predicate<Throwable>>[]) annotation.recoverableFilters()),false)
                    .withImmediatelyUnrecoverableFilters(
                            wrapAndMerge(
                                    annotation.immediatelyUnrecoverableThrowables(),
                                    (Class<Predicate<Throwable>>[]) annotation.immediatelyUnrecoverableFilters()))
                    .withCheckNestedThrowables(annotation.checkNestedThrowables());
        }

        /**
         * Can be used to selectively <i>override</i> current builder state with a more 'specific' annotation - this can
         * be used to avoid <i>copy-paste</i> in specifying where there is a large degree of commonality but certain key
         * variables disagree; for instance {@link #expirationDurationMillis} depending on a service methods complexity.
         * <p/>
         *
         * @param annotation the {@link Retryable} annotation whose <i>non-default</i> components will override existing
         * values
         * @return the builder - with its internal state modified to reflect the new <i>non-default</i> configuration
         * elements
         * @see Retryable
         */
        public Builder override(final Retryable annotation) {
            //enable default suppression

            boolean merge = annotation.mergeThrowables();
            Collection<Predicate<Throwable>> inboundRecoverableFilters = wrapAndMerge(
                    annotation.recoverableThrowables(),
                    (Class<Predicate<Throwable>>[]) annotation.recoverableFilters());
            // Inbound recoverable filters will either be:
            //   - explicitly defined as empty, and the result of the merge will be empty
            //   - will both be defaults, and will result in list with a single 'Any' instance
            //   - will be correctly configured with either (or both) collections containing non-zero entries of
            //     Throwables/Filters NOT including 'Any' instance
            //   - will be INcorrectly configured with either (or both) collections containing non-zero entries of
            //     Throwables/Filters INCLUDING 'Any' instance - which would be pointless since the
            //     Throwables/Filters which are NOT 'Any' instance would be ignored since Any covers
            //     ALL throwables, making the inclusion of other Throwables/Filters pointless.
            if (inboundRecoverableFilters.size() == 1 && inboundRecoverableFilters.iterator().next() instanceof Any) {
                // We will be merging and should exclude 'Any' instance if it was added
                // This is not necessary for unrecoverable filters since the @Retryable annotation does not specify Any by default.
                inboundRecoverableFilters = Collections.emptyList();
            }
            // If the override does not specify anything, inherit from the 'base' @Retryable
            boolean mergeRecoverable = merge || inboundRecoverableFilters.isEmpty();

            Collection<Predicate<Throwable>> inboundUnrecoverableFilters = wrapAndMerge(
                    annotation.immediatelyUnrecoverableThrowables(),
                    (Class<Predicate<Throwable>>[]) annotation.immediatelyUnrecoverableFilters());
            // If the override does not specify anything, inherit from the 'base' @Retryable
            boolean mergeUnrecoverable = merge || inboundUnrecoverableFilters.isEmpty();

            return this.withBackoffCoefficient(annotation.backoffCoefficient(), true)
                    //already has default suppression
                    .withJitterScalingCoefficient(annotation.jitterScalingCoefficient())
                    //enable (calculated) default suppression
                    .withMaxDelayMillis(annotation.maxDelyUnit().toMillis(annotation.maxDelay()), true)
                    //enable default suppression
                    .withMaxAttempts(annotation.maxAttempts(), true)
                    //enable (calculated) default suppression
                    .withMultiplierMillis(annotation.delayUnit().toMillis(annotation.delay()), true)
                    //enable (calculated) default suppression
                    .withExpirationDurationMillis(annotation.expirationDurationUnit()
                            .toMillis(annotation.expirationDuration()), true)
                    //delegate down replace or merge
                    .withRecoverableAnnotationFilters(inboundRecoverableFilters, mergeRecoverable)

                    //delegate down replace or merge
                    .withImmediatelyUnrecoverableFilters(inboundUnrecoverableFilters, mergeUnrecoverable)

                    .withCheckNestedThrowables(annotation.checkNestedThrowables(),
                            annotation.forceDisableNestedThrowables());
        }

        /**
         * Provides a deep-copy clone (non-backed) of the Builder. This is designed to support the Builder being used
         * in a <i>prototyping</i> capacity. whereby a partially completed instance can be created and then duplicated
         * for multiple child instances - who can then be 'specialized' independently.
         *
         * <b>NOTE</b>: the duplicate method imposes some additional constraints these are as follows:
         * <ul>
         * 	<li>the <i>{@link #withDelayPostProcessor(DelayPostProcessor)}</i> - if provided must be stateless; as there
         * is no factory by which to clone it</li>
         * </ul>
         *
         * @return a new, not-null, not-backed builder instance created with the same properties as the invocation
         * target.
         */
        public Builder duplicate() {
            Builder clone = new Builder();
            clone.backoffCoefficient 	   				= backoffCoefficient;
            clone.multiplierMillis   	   				= multiplierMillis;
            clone.maxAttempts 	     	   				= maxAttempts;
            clone.maxDelayMillis 	 	   				= maxDelayMillis;
            clone.expirationDurationMillis 				= expirationDurationMillis;
            clone.delayPostProcessor 	   				= delayPostProcessor;
            clone.immediatelyUnrecoverableFilters       = ImmutableList.copyOf(immediatelyUnrecoverableFilters);
            clone.recoverableFilters                    = ImmutableList.copyOf(recoverableFilters);
            clone.checkNestedThrowables 				= checkNestedThrowables;
            return clone;
        }

        public Builder withBackoffCoefficient(double backoffCoefficient) {
            return withBackoffCoefficient(backoffCoefficient, false);
        }

        private Builder withBackoffCoefficient(double backoffCoefficient, boolean suppressDefault) {
            if (!suppressDefault || RetryableDefaultValues.DEFAULTS.backoffCoefficient() != backoffCoefficient) {
                this.backoffCoefficient = backoffCoefficient;
            }
            return this;
        }

        private Builder withJitterScalingCoefficient(double jitterScalingCoefficient) {
            if (jitterScalingCoefficient != 0.0) {
                this.delayPostProcessor = new RandomAndLessThanDelayPostProcessor(jitterScalingCoefficient);
            }
            return this;
        }

        public Builder withMultiplierMillis(long multiplierMillis) {
            return withMultiplierMillis(multiplierMillis, false);
        }

        private Builder withMultiplierMillis(long multiplierMillis, boolean suppressDefault) {
            if (!suppressDefault || RetryableDefaultValues.DEFAULT_DELAY != multiplierMillis) {
                this.multiplierMillis = multiplierMillis;
            }
            return this;
        }

        public Builder withMaxAttempts(int maxAttempts) {
            return withMaxAttempts(maxAttempts, false);
        }

        private Builder withMaxAttempts(int maxAttempts, boolean suppressDefault) {
            if (!suppressDefault || RetryableDefaultValues.DEFAULTS.maxAttempts() != maxAttempts) {
                this.maxAttempts = maxAttempts;
            }
            return this;
        }

        public Builder withMaxDelayMillis(long maxDelayMillis) {
            return withMaxDelayMillis(maxDelayMillis, false);
        }

        private Builder withMaxDelayMillis(long maxDelayMillis, boolean suppressDefault) {
            if (!suppressDefault || RetryableDefaultValues.DEFAULT_MAX_DELAY != maxDelayMillis) {
                this.maxDelayMillis = maxDelayMillis;
            }
            return this;
        }

        public Builder withDelayPostProcessor(DelayPostProcessor delayPostProcessor) {
            this.delayPostProcessor = delayPostProcessor;
            return this;
        }

        public Builder withImmediatelyUnrecoverableThrowables(
                Collection<Class<? extends Throwable>> immediatelyUnrecoverableThrowables) {
            return withImmediatelyUnrecoverableThrowables(ImmutableList.copyOf(immediatelyUnrecoverableThrowables),
                    false);
        }

        /**
         * Merges the provided collection of unrecoverable filters with the internally stored collection. Merge is
         * enabled by default to allow usage of Throwable subclass based filtering.
         * @param immediatelyUnrecoverableFilters to merge
         * @return this builder with modified {@link Builder#recoverableFilters}
         */
        public Builder withImmediatelyUnrecoverableFilters(
                Predicate<Throwable>...immediatelyUnrecoverableFilters) {
            return withImmediatelyUnrecoverableFilters(Arrays.asList(immediatelyUnrecoverableFilters),
                    true);
        }

        /**
         * Merges the provided collection of unrecoverable filters with the internally stored collection. Merge is
         * enabled by default to allow usage of Throwable subclass based filtering.
         * @param immediatelyUnrecoverableFilters to merge
         * @return this builder with modified {@link Builder#recoverableFilters}
         */
        public Builder withImmediatelyUnrecoverableFilters(
                Collection<Predicate<Throwable>> immediatelyUnrecoverableFilters) {
            return withImmediatelyUnrecoverableFilters(ImmutableList.copyOf(immediatelyUnrecoverableFilters),
                    true);
        }

        @SafeVarargs
        public final Builder withImmediatelyUnrecoverableThrowables(
                Class<? extends Throwable>... immediatelyUnrecoverableThrowables) {
            return withImmediatelyUnrecoverableThrowables(ImmutableList.copyOf(immediatelyUnrecoverableThrowables),
                    false);
        }

        private Builder withImmediatelyUnrecoverableThrowables(
                Collection<Class<? extends Throwable>> immediatelyUnrecoverableThrowables,
                boolean merge) {
            return withImmediatelyUnrecoverableFilters(isInstanceFilters(immediatelyUnrecoverableThrowables),merge);
        }

        private Builder withImmediatelyUnrecoverableFilters(
                Collection<Predicate<Throwable>> immediatelyUnrecoverableFilters,
                boolean merge) {
            if (merge || immediatelyUnrecoverableFilters.isEmpty()) {
                this.immediatelyUnrecoverableFilters = merge(
                        this.immediatelyUnrecoverableFilters,
                        immediatelyUnrecoverableFilters);
            } else {
                this.immediatelyUnrecoverableFilters = immediatelyUnrecoverableFilters;
            }
            return this;
        }

        public Builder withRecoverableThrowables(Collection<Class<? extends Throwable>> recoverableThrowables) {
            return withRecoverableThrowables(ImmutableList.copyOf(recoverableThrowables), false);
        }

        private Builder withRecoverableAnnotationFilters(Collection<Predicate<Throwable>> recoverableFilters, boolean merge) {
            return withRecoverableFilters(ImmutableList.copyOf(recoverableFilters), merge);
        }

        /**
         * Merges the provided collection of recoverable filters with the internally stored collection.
         * @param recoverableFilters to merge.
         * @return this builder with modified {@link Builder#recoverableFilters}
         */
        public Builder withRecoverableFilters(Predicate<Throwable>...recoverableFilters) {
            return withRecoverableFilters(Arrays.asList(recoverableFilters),true);
        }

        /**
         * Merges the provided collection of recoverable filters with the internally stored collection.
         * @param recoverableFilters to merge.
         * @return this builder with modified {@link Builder#recoverableFilters}
         */
        public Builder withRecoverableFilters(Collection<Predicate<Throwable>> recoverableFilters) {
            return withRecoverableFilters(recoverableFilters,true);
        }

        private Builder withRecoverableThrowables(Collection<Class<? extends Throwable>> recoverableThrowables,
                                                  boolean merge) {
            return withRecoverableFilters(isInstanceFilters(recoverableThrowables),merge);
        }

        private Builder withRecoverableFilters(
                Collection<Predicate<Throwable>> recoverableFilters,
                boolean merge) {
            if (merge || recoverableFilters.isEmpty()) {
                this.recoverableFilters = merge(
                        (Collection<Predicate<Throwable>>) this.recoverableFilters,
                        recoverableFilters);
            } else {
                this.recoverableFilters = recoverableFilters;
            }
            autoRemoveDefaultRetryableFilter();
            return this;
        }

        /**
         * The default retryableFilter collection produced by the {@link Retryable} annotation will
         * contain a {@link Any} to auto-retry any exception with the default
         * configuration.
         * Once a downstream consumer adds any non-default filter of any kind, we should remove the
         * default filter as it will auto-retry --any-- exception, which is not the desired
         * behavior.
         */
        private void autoRemoveDefaultRetryableFilter() {
            if (this.recoverableFilters.size() < 2) {
                // Nothing to do - if there is one or zero filters, then we should not remove the default filter.
                return;
            }
            if (!this.recoverableFilters.contains(Any.INSTANCE)) {
                // Nothing to do.
                return;
            }
            // There are 2 or more filters. One of them could be the default, which should potentially be removed.
            this.recoverableFilters = this.recoverableFilters
                    .stream()
                    .filter(f -> f != Any.INSTANCE)
                    .collect(Collectors.toList());
        }

        private <T> Collection<T> merge(Collection<T> source, Collection<T> additions) {
            List<T> mutableSource = new ArrayList<>(source);
            mutableSource.addAll(additions);
            return ImmutableList.copyOf(mutableSource);
        }

        @SafeVarargs
        public final Builder withRecoverableThrowables(Class<? extends Throwable>... recoverableThrowables) {
            return withRecoverableFilters(isInstanceFilters(Arrays.asList(recoverableThrowables)),false);
        }

        @SafeVarargs
        public final Builder withRecoverableFilters(boolean merge, Predicate<Throwable>... recoverableFilters) {
            return withRecoverableFilters(Arrays.asList(recoverableFilters),merge);
        }

        public Builder withCheckNestedThrowables(boolean checkNestedThrowables) {
            return withCheckNestedThrowables(checkNestedThrowables, false);
        }

        private Builder withCheckNestedThrowables(boolean checkNestedThrowables, boolean forceSuppress) {
            //if we are forcing the suppression - push to false
            if (forceSuppress) {
                this.checkNestedThrowables = false;
            } else {
                //otherwise take whichever is higher between the current value and the provided
                this.checkNestedThrowables |= checkNestedThrowables;
            }
            return this;
        }

        public Builder withExpirationDurationMillis(long expirationDurationMillis) {
            return withExpirationDurationMillis(expirationDurationMillis, false);
        }

        private Builder withExpirationDurationMillis(long expirationDurationMillis, boolean suppressDefault) {
            if (!suppressDefault || RetryableDefaultValues.DEFAULT_EXPIRY != expirationDurationMillis) {
                this.expirationDurationMillis = expirationDurationMillis;
            }
            return this;
        }

        /**
         * @param delayMillis
         * @return {@link Builder} that performs retries at a fixed delay without any jitter.
         *         This is equivalent to calling:
         *         - {@link #withBackoffCoefficient(double)}
         *         - {@link #withMultiplierMillis(long)}
         *         - {@link #withMaxDelayMillis(long)}
         *         - {@link #withDelayPostProcessor(DelayPostProcessor)}
         */
        public Builder withFixedDelay(long delayMillis) {
            return withBackoffCoefficient(1.0)
                    .withMultiplierMillis(delayMillis)
                    .withMaxDelayMillis(delayMillis)
                    .withDelayPostProcessor(createIdentityDelayPostProcessor());
        }

    }
}

