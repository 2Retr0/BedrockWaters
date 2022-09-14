package retr0.bedrockwaters.util;

import net.minecraft.util.Util;

import static net.minecraft.util.math.MathHelper.*;

/**
 * A utility which performs Hermite interpolation between two values over the course of a specified duration.
 */
public record SmoothStepUtil(float initialValue, float targetValue, float deltaTime, long startTime) {
    /**
     * @param initialValue The initial value for interpolation.
     * @param targetValue The target value for interpolation.
     * @param deltaTime The total time in milliseconds over which the interpolation should occur.
     * @param offset An offset time in milliseconds before interpolation would start (defaults to {@code 0f}).
     */
    public SmoothStepUtil(float initialValue, float targetValue, float deltaTime, float offset) {
        this(initialValue, targetValue, deltaTime, Util.getMeasuringTimeMs() + ((long) offset));
    }

    /**
     * @see SmoothStepUtil#SmoothStepUtil
     */
    public SmoothStepUtil(float initialValue, float targetValue, float deltaTime) {
        this(initialValue, targetValue, deltaTime, 0f);
    }

    /**
     * Used to represent an already completed transition.
     * @see SmoothStepUtil#SmoothStepUtil
     */
    public SmoothStepUtil(float finalValue) { this(finalValue, finalValue, 0.001f, 0L); }



    /**
     * @return The current value based on interpolation progress.
     */
    public float currentValue() {
        // Smooth step between start and target values if the target value is not yet reached.
        var currentDelta = clamp((Util.getMeasuringTimeMs() - startTime) / deltaTime, 0f, 1f);

        return (currentDelta > 1f) ? targetValue : smoothStep(currentDelta, initialValue, targetValue);
    }



    /**
     * Performs Hermite interpolation between two values as used in GLSL's smoothstep().
     * <a href="https://en.wikipedia.org/wiki/Smoothstep">Source</a>
     *
     * @param t The interpolation parameter between 0 and 1.
     * @param a The initial value for interpolation.
     * @param b The target value for interpolation.
     * @return The current value based on {@code t}.
     */
    public static float smoothStep(float t, float a, float b) { return lerp(t * t * (3f - 2f * t), a, b); }
}
