package com.eliotlash.mclib.utils;

/**
 * Interpolation methods
 *
 * This class is responsible for doing different kind of interpolations. Cubic
 * interpolation code was from website below, but BauerCam also uses this code.
 *
 * @author mchorse
 * @see <a href="http://paulbourke.net/miscellaneous/interpolation/">Interpolation</a>
 * @see <a href="https://github.com/daipenger/BauerCam">BauerCam</a>
 */
public class Interpolations
{
    /**
     * Linear interpolation
     */
    public static float lerp(float a, float b, float position)
    {
        return a + (b - a) * position;
    }

    /**
     * Special interpolation method for interpolating yaw. The problem with yaw,
     * is that it may go in the "wrong" direction when having, for example,
     * -170 (as a) and 170 (as b) degress or other way around (170 and -170).
     *
     * This interpolation method fixes this problem.
     */
    public static float lerpYaw(float a, float b, float position)
    {
        a = MathHelper.wrapDegrees(a);
        b = MathHelper.wrapDegrees(b);

        return lerp(a, normalizeYaw(a, b), position);
    }

    /**
     * Cubic interpolation using Hermite between y1 and y2. Taken from paul's
     * website.
     *
     * @param y0 - points[x-1]
     * @param y1 - points[x]
     * @param y2 - points[x+1]
     * @param y3 - points[x+2]
     * @param x - step between 0 and 1
     */
    public static double cubicHermite(double y0, double y1, double y2, double y3, double x)
    {
        double a = -0.5 * y0 + 1.5 * y1 - 1.5 * y2 + 0.5 * y3;
        double b = y0 - 2.5 * y1 + 2 * y2 - 0.5 * y3;
        double c = -0.5 * y0 + 0.5 * y2;

        return ((a * x + b) * x + c) * x + y1;
    }

    /**
     * Yaw normalization for cubic interpolation
     */
    public static double cubicHermiteYaw(float y0, float y1, float y2, float y3, float position)
    {
        y0 = MathHelper.wrapDegrees(y0);
        y1 = MathHelper.wrapDegrees(y1);
        y2 = MathHelper.wrapDegrees(y2);
        y3 = MathHelper.wrapDegrees(y3);

        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);

        return cubicHermite(y0, y1, y2, y3, position);
    }

    /**
     * Cubic interpolation between y1 and y2. Taken from paul's website.
     *
     * @param y0 - points[x-1]
     * @param y1 - points[x]
     * @param y2 - points[x+1]
     * @param y3 - points[x+2]
     * @param x - step between 0 and 1
     */
    public static float cubic(float y0, float y1, float y2, float y3, float x)
    {
        float a = y3 - y2 - y0 + y1;
        float b = y0 - y1 - a;
        float c = y2 - y0;

        return ((a * x + b) * x + c) * x + y1;
    }

    /**
     * Yaw normalization for cubic interpolation
     */
    public static float cubicYaw(float y0, float y1, float y2, float y3, float position)
    {
        y0 = MathHelper.wrapDegrees(y0);
        y1 = MathHelper.wrapDegrees(y1);
        y2 = MathHelper.wrapDegrees(y2);
        y3 = MathHelper.wrapDegrees(y3);

        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);

        return cubic(y0, y1, y2, y3, position);
    }

    /**
     * Calculate X value for given T using some brute force algorithm...
     * This method should be precise enough
     *
     * @param x1 - control point of initial value
     * @param x2 - control point of final value
     * @param t - time (should be 0..1)
     * @param epsilon - delta that would satisfy the approximation
     */
    public static float bezierX(float x1, float x2, float t, final float epsilon)
    {
        float x = t;
        float init = bezier(0, x1, x2, 1, t);
        float factor = Math.copySign(0.1F, t - init);

        while (Math.abs(t - init) > epsilon)
        {
            float oldFactor = factor;

            x += factor;
            init = bezier(0, x1, x2, 1, x);

            if (Math.copySign(factor, t - init) != oldFactor)
            {
                factor *= -0.25F;
            }
        }

        return x;
    }

    /**
     * Calculate X value for given T using default epsilon value. See
     * other overload method for more information.
     */
    public static float bezierX(float x1, float x2, float t)
    {
        return bezierX(x1, x2, t, 0.0005F);
    }

    /**
     * Calculate cubic bezier from given variables
     *
     * @param x1 - initial value
     * @param x2 - control point of initial value
     * @param x3 - control point of final value
     * @param x4 - final value
     * @param t - time (should be 0..1)
     */
    public static float bezier(float x1, float x2, float x3, float x4, float t)
    {
        float t1 = lerp(x1, x2, t);
        float t2 = lerp(x2, x3, t);
        float t3 = lerp(x3, x4, t);
        float t4 = lerp(t1, t2, t);
        float t5 = lerp(t2, t3, t);

        return lerp(t4, t5, t);
    }

    /**
     * Normalize yaw rotation (argument {@code b}) based on the previous
     * yaw rotation.
     */
    public static float normalizeYaw(float a, float b)
    {
        float diff = a - b;

        if (diff > 180 || diff < -180)
        {
            diff = Math.copySign(360 - Math.abs(diff), diff);

            return a + diff;
        }

        return b;
    }

    /**
     * Envelope function allows to create simple attack, sustain and release function.
     *
     * This version only goes from 0 to duration with fade in/out being the same
     */
    public static float envelope(float x, float duration, float fades)
    {
        return envelope(x, 0, fades, duration - fades, duration);
    }

    /**
     * Envelope function allows to create simple attack, sustain and release function.
     *
     * This advanced version allows you to specify a more customized range
     */
    public static float envelope(float x, float lowIn, float lowOut, float highIn, float highOut)
    {
        if (x < lowIn || x > highOut) return 0;
        if (x < lowOut) return (x - lowIn) / (lowOut - lowIn);
        if (x > highIn) return 1 - (x - highIn) / (highOut - highIn);

        return 1;
    }

    /* --- Double versions of the functions --- */

    /**
     * Linear interpolation
     */
    public static double lerp(double a, double b, double position)
    {
        return a + (b - a) * position;
    }

    /**
     * Special interpolation method for interpolating yaw. The problem with yaw,
     * is that it may go in the "wrong" direction when having, for example,
     * -170 (as a) and 170 (as b) degress or other way around (170 and -170).
     *
     * This interpolation method fixes this problem.
     */
    public static double lerpYaw(double a, double b, double position)
    {
        a = MathHelper.wrapDegrees(a);
        b = MathHelper.wrapDegrees(b);

        return lerp(a, normalizeYaw(a, b), position);
    }

    /**
     * Cubic interpolation between y1 and y2. Taken from paul's website.
     *
     * @param y0 - points[x-1]
     * @param y1 - points[x]
     * @param y2 - points[x+1]
     * @param y3 - points[x+2]
     * @param x - step between 0 and 1
     */
    public static double cubic(double y0, double y1, double y2, double y3, double x)
    {
        double a = y3 - y2 - y0 + y1;
        double b = y0 - y1 - a;
        double c = y2 - y0;

        return ((a * x + b) * x + c) * x + y1;
    }

    /**
     * Yaw normalization for cubic interpolation
     */
    public static double cubicYaw(double y0, double y1, double y2, double y3, double position)
    {
        y0 = MathHelper.wrapDegrees(y0);
        y1 = MathHelper.wrapDegrees(y1);
        y2 = MathHelper.wrapDegrees(y2);
        y3 = MathHelper.wrapDegrees(y3);

        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);

        return cubic(y0, y1, y2, y3, position);
    }

    /**
     * Calculate X value for given T using some brute force algorithm...
     * This method should be precise enough
     *
     * @param x1 - control point of initial value
     * @param x2 - control point of final value
     * @param t - time (should be 0..1)
     * @param epsilon - delta that would satisfy the approximation
     */
    public static double bezierX(double x1, double x2, double t, final double epsilon)
    {
        double x = t;
        double init = bezier(0, x1, x2, 1, t);
        double factor = Math.copySign(0.1F, t - init);

        while (Math.abs(t - init) > epsilon)
        {
            double oldFactor = factor;

            x += factor;
            init = bezier(0, x1, x2, 1, x);

            if (Math.copySign(factor, t - init) != oldFactor)
            {
                factor *= -0.25F;
            }
        }

        return x;
    }

    /**
     * Calculate X value for given T using default epsilon value. See
     * other overload method for more information.
     */
    public static double bezierX(double x1, double x2, float t)
    {
        return bezierX(x1, x2, t, 0.0005F);
    }

    /**
     * Calculate cubic bezier from given variables
     *
     * @param x1 - initial value
     * @param x2 - control point of initial value
     * @param x3 - control point of final value
     * @param x4 - final value
     * @param t - time (should be 0..1)
     */
    public static double bezier(double x1, double x2, double x3, double x4, double t)
    {
        double t1 = lerp(x1, x2, t);
        double t2 = lerp(x2, x3, t);
        double t3 = lerp(x3, x4, t);
        double t4 = lerp(t1, t2, t);
        double t5 = lerp(t2, t3, t);

        return lerp(t4, t5, t);
    }

    /**
     * Normalize yaw rotation (argument {@code b}) based on the previous
     * yaw rotation.
     */
    public static double normalizeYaw(double a, double b)
    {
        double diff = a - b;

        if (diff > 180 || diff < -180)
        {
            diff = Math.copySign(360 - Math.abs(diff), diff);

            return a + diff;
        }

        return b;
    }

    /**
     * Envelope function allows to create simple attack, sustain and release function.
     *
     * This version only goes from 0 to duration with fade in/out being the same
     */
    public static double envelope(double x, double duration, double fades)
    {
        return envelope(x, 0, fades, duration - fades, duration);
    }

    /**
     * Envelope function allows to create simple attack, sustain and release function.
     *
     * This advanced version allows you to specify a more customized range
     */
    public static double envelope(double x, double lowIn, double lowOut, double highIn, double highOut)
    {
        if (x < lowIn || x > highOut) return 0;
        if (x < lowOut) return (x - lowIn) / (lowOut - lowIn);
        if (x > highIn) return 1 - (x - highIn) / (highOut - highIn);

        return 1;
    }
}
