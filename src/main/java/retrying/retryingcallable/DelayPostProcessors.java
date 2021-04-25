package retrying.retryingcallable;

public final class DelayPostProcessors {

    /**
     * @return {@link DelayPostProcessor} that always returns the same input (i.e., no jitter).
     */
    public static DelayPostProcessor createIdentityDelayPostProcessor() {
        return new RandomAndLessThanDelayPostProcessor(0);
    }

    /**
     * @return {@link #createRandomAndLessThanDelayPostProcessor(double)} with a jitter scaling coefficient of .5.
     * Resulting delays will have a value in [.5 delay, delay).
     */
    public static DelayPostProcessor createRandomAndLessThanDelayPostProcessor() {
        return createRandomAndLessThanDelayPostProcessor(.5);
    }

    /**
     * @param jitterScalingCoefficient
     * @return {@link RandomAndLessThanDelayPostProcessor} with the provided randomScalingCoefficient.
     * @see RandomAndLessThanDelayPostProcessor#RandomAndLessThanDelayPostProcessor(double)
     */
    public static DelayPostProcessor createRandomAndLessThanDelayPostProcessor(double jitterScalingCoefficient) {
        return new RandomAndLessThanDelayPostProcessor(jitterScalingCoefficient);
    }

    /**
     * This is a utility class that shouldn't be instantiated.
     * Effective Java Item #4: enforce non-instantiability.
     */
    private DelayPostProcessors() {
        throw new AssertionError();
    }
}

