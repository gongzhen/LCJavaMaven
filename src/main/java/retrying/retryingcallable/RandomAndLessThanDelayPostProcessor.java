package retrying.retryingcallable;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
class RandomAndLessThanDelayPostProcessor implements DelayPostProcessor {

    private final double jitterScalingCoefficient;

    /**
     * @param jitterScalingCoefficient See {@link #getDelayMillisFor(long)} for explanation.
     */
    public RandomAndLessThanDelayPostProcessor(double jitterScalingCoefficient) {
        Preconditions.checkArgument(
                0 <= jitterScalingCoefficient && jitterScalingCoefficient <= 1,
                "0 <= jitterScalingCoefficient <= 1 but was " + jitterScalingCoefficient
        );
        this.jitterScalingCoefficient = jitterScalingCoefficient;
    }

    /**
     * @return the provided delay multiplied by a delayCoefficient where
     * delayCoefficient = 1 - {@link #jitterScalingCoefficient} * random(0,1]
     * When {@link #jitterScalingCoefficient} == 0, the delayCoefficient will be 0, and the returned delay will be provided delay.
     * When {@link #jitterScalingCoefficient} == 1, the delayCoefficient will be [0,1), and the returned delay will be [0,delay)
     */
    @Override
    public long getDelayMillisFor(@Nonnegative long delay) {
        double delayCoefficient = 1 - (jitterScalingCoefficient * (1 - Math.random()));
        return (long)(delayCoefficient * delay);
    }
}
