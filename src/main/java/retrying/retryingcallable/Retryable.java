package retrying.retryingcallable;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Retryable {

    /**
     * Maximum number of attempts to complete method execution (including initial attempt). Zero means no maximum. Defaults to 2.
     */
    int maxAttempts() default 2;

    /**
     * The amount of time to delay between attempts. The {@link TimeUnit} is controlled by the {@link #delayUnit()} property. Defaults to 0.
     */
    long delay() default 0;

    /**
     * The {@link TimeUnit} that describes the {@link #delay()} parameter. Defaults to {@link TimeUnit#MILLISECONDS}.
     */
    TimeUnit delayUnit() default TimeUnit.MILLISECONDS;

    /**
     * The maximum delay between retries. The {@link TimeUnit} is controlled by the {@link #maxDelyUnit()} property. Defaults to {@link Long#MAX_VALUE}.
     */
    long maxDelay() default Long.MAX_VALUE;

    /**
     * The {@link TimeUnit} that describes the {@link #maxDelay()} parameter. Defaults to {@link TimeUnit#MILLISECONDS}.
     */
    TimeUnit maxDelyUnit() default TimeUnit.MILLISECONDS;

    /**
     * The maximum amount of elapsed time before retrying stops. The {@link TimeUnit} is controlled by the
     * {@link #expirationDurationUnit()} property. but can be changed via the {@link #expirationDurationUnit()} property. Defaults to {@link Long#MAX_VALUE}.
     */
    long expirationDuration() default Long.MAX_VALUE;

    /**
     * The {@link TimeUnit} that describes the {@link #expirationDuration()} parameter. Defaults to {@link TimeUnit#MILLISECONDS}.
     */
    TimeUnit expirationDurationUnit() default TimeUnit.MILLISECONDS;

    /**
     * {@link Throwable}s that should be automatically caught and retried.
     */
    Class<? extends Throwable>[] recoverableThrowables() default { };

    /**
     * Implementations of {@link Predicate<Throwable>} which will determine recoverable Exceptions at runtime. Defaults to NONE.
     */
    Class<? extends Predicate<Throwable>>[] recoverableFilters() default {Any.class};

    /**
     * {@link Throwable}s that should be never be caught and retried. Defaults to NONE.
     */
    Class<? extends Throwable>[] immediatelyUnrecoverableThrowables() default {};

    /**
     * Implementations of {@link Predicate<Throwable>} which will determine exceptions which should be caught and retried. Defaults to NONE.
     */
    Class<? extends Predicate<Throwable>>[] immediatelyUnrecoverableFilters() default {};

    /**
     * Whether to check the {@link Throwable}s that are nested within the root {@link Throwable}. <p/>
     *
     * <b>NOTE</b>: In the case of using composition of the annotation, checkNestedThrowables can be overriden from
     * 'false to true' by the more tightly binding annotation. However if the user desires to override from 'true to
     * false' (i.e. overriding to <i>disable</i> nested throwable checking for a specific method) then the
     * {@link #forceDisableNestedThrowables()} must be used instead. Defaults to false.
     *
     * @see #forceDisableNestedThrowables()
     */
    boolean checkNestedThrowables() default false;

    /**
     * Whether to <i>force</i> the checking of nested throwables to be ignored. <p/>
     *
     * this is an explicit override of the
     * {@link #checkNestedThrowables()} due to the inability to have an annotation return a {@link Boolean} which can
     * be defaulted to NULL. It is likely to only be used on a <i>method</i> level use of an annotation in order to
     * override the <i>class</i> level annotation. However if an interceptor is configured with a <i>general</i> policy
     * that enables disabling nested throwables then it may still be possible for a <i>class</i> level annotation to
     * meaningfully set this value.
     * <br/> It will not be meaningful to set this on a <i>class</i> level to try and override a <i>method</i> level
     * setting as cascading policies treat finer granularity scopes with preference.  Defaults to false.
     *
     *
     */
    boolean forceDisableNestedThrowables() default false;

    /**
     * Multiplier base used to determine increase in retry delays (used for exponential backoff delays). Defaults to 1.
     * (i.e., no increase).
     */
    double backoffCoefficient() default 1;

    /**
     * Jitter scaling coefficient used to determine how much jitter should be applied to retry delays. The number should
     * be between 0 and 1. Defaults to 0 (i.e. no jitter).
     */
    double jitterScalingCoefficient() default 0.0;

    /**
     * Processing instruction for determining <i>how</i> to roll up or 'layer' the throwables specified in
     * {@link #immediatelyUnrecoverableThrowables()} and {@link #recoverableThrowables()} when multiple Retryable
     * annotations are present; into a single {@link RetryPolicy}. <p/>
     *
     * By default if throwables are specified in a more specific annotation they will <i>override</i> (replace) those
     * specified in the less specific annotation. If this flag is set, however, the throwables will be <i>merged</i>.
     *
     * @since 1.2
     */
    boolean mergeThrowables() default false;
}
