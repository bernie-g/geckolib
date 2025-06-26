package anightdazingzoroark.riftlib.molang.utils;

public class Interpolations {
    public static float lerp(float a, float b, float position) {
        return a + (b - a) * position;
    }

    public static float lerpYaw(float a, float b, float position) {
        a = MathHelper.wrapDegrees(a);
        b = MathHelper.wrapDegrees(b);
        return lerp(a, normalizeYaw(a, b), position);
    }

    public static double cubicHermite(double y0, double y1, double y2, double y3, double x) {
        double a = (double)-0.5F * y0 + (double)1.5F * y1 - (double)1.5F * y2 + (double)0.5F * y3;
        double b = y0 - (double)2.5F * y1 + (double)2.0F * y2 - (double)0.5F * y3;
        double c = (double)-0.5F * y0 + (double)0.5F * y2;
        return ((a * x + b) * x + c) * x + y1;
    }

    public static double cubicHermiteYaw(float y0, float y1, float y2, float y3, float position) {
        y0 = MathHelper.wrapDegrees(y0);
        y1 = MathHelper.wrapDegrees(y1);
        y2 = MathHelper.wrapDegrees(y2);
        y3 = MathHelper.wrapDegrees(y3);
        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);
        return cubicHermite((double)y0, (double)y1, (double)y2, (double)y3, (double)position);
    }

    public static float cubic(float y0, float y1, float y2, float y3, float x) {
        float a = y3 - y2 - y0 + y1;
        float b = y0 - y1 - a;
        float c = y2 - y0;
        return ((a * x + b) * x + c) * x + y1;
    }

    public static float cubicYaw(float y0, float y1, float y2, float y3, float position) {
        y0 = MathHelper.wrapDegrees(y0);
        y1 = MathHelper.wrapDegrees(y1);
        y2 = MathHelper.wrapDegrees(y2);
        y3 = MathHelper.wrapDegrees(y3);
        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);
        return cubic(y0, y1, y2, y3, position);
    }

    public static float bezierX(float x1, float x2, float t, float epsilon) {
        float x = t;
        float init = bezier(0.0F, x1, x2, 1.0F, t);
        float factor = Math.copySign(0.1F, t - init);

        while(Math.abs(t - init) > epsilon) {
            x += factor;
            init = bezier(0.0F, x1, x2, 1.0F, x);
            if (Math.copySign(factor, t - init) != factor) {
                factor *= -0.25F;
            }
        }

        return x;
    }

    public static float bezierX(float x1, float x2, float t) {
        return bezierX(x1, x2, t, 5.0E-4F);
    }

    public static float bezier(float x1, float x2, float x3, float x4, float t) {
        float t1 = lerp(x1, x2, t);
        float t2 = lerp(x2, x3, t);
        float t3 = lerp(x3, x4, t);
        float t4 = lerp(t1, t2, t);
        float t5 = lerp(t2, t3, t);
        return lerp(t4, t5, t);
    }

    public static float normalizeYaw(float a, float b) {
        float diff = a - b;
        if (!(diff > 180.0F) && !(diff < -180.0F)) {
            return b;
        } else {
            diff = Math.copySign(360.0F - Math.abs(diff), diff);
            return a + diff;
        }
    }

    public static float envelope(float x, float duration, float fades) {
        return envelope(x, 0.0F, fades, duration - fades, duration);
    }

    public static float envelope(float x, float lowIn, float lowOut, float highIn, float highOut) {
        if (!(x < lowIn) && !(x > highOut)) {
            if (x < lowOut) {
                return (x - lowIn) / (lowOut - lowIn);
            } else {
                return x > highIn ? 1.0F - (x - highIn) / (highOut - highIn) : 1.0F;
            }
        } else {
            return 0.0F;
        }
    }

    public static double lerp(double a, double b, double position) {
        return a + (b - a) * position;
    }

    public static double lerpYaw(double a, double b, double position) {
        a = MathHelper.wrapDegrees(a);
        b = MathHelper.wrapDegrees(b);
        return lerp(a, normalizeYaw(a, b), position);
    }

    public static double cubic(double y0, double y1, double y2, double y3, double x) {
        double a = y3 - y2 - y0 + y1;
        double b = y0 - y1 - a;
        double c = y2 - y0;
        return ((a * x + b) * x + c) * x + y1;
    }

    public static double cubicYaw(double y0, double y1, double y2, double y3, double position) {
        y0 = MathHelper.wrapDegrees(y0);
        y1 = MathHelper.wrapDegrees(y1);
        y2 = MathHelper.wrapDegrees(y2);
        y3 = MathHelper.wrapDegrees(y3);
        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);
        return cubic(y0, y1, y2, y3, position);
    }

    public static double bezierX(double x1, double x2, double t, double epsilon) {
        double x = t;
        double init = bezier((double)0.0F, x1, x2, (double)1.0F, t);
        double factor = Math.copySign((double)0.1F, t - init);

        while(Math.abs(t - init) > epsilon) {
            x += factor;
            init = bezier((double)0.0F, x1, x2, (double)1.0F, x);
            if (Math.copySign(factor, t - init) != factor) {
                factor *= (double)-0.25F;
            }
        }

        return x;
    }

    public static double bezierX(double x1, double x2, float t) {
        return bezierX(x1, x2, (double)t, (double)5.0E-4F);
    }

    public static double bezier(double x1, double x2, double x3, double x4, double t) {
        double t1 = lerp(x1, x2, t);
        double t2 = lerp(x2, x3, t);
        double t3 = lerp(x3, x4, t);
        double t4 = lerp(t1, t2, t);
        double t5 = lerp(t2, t3, t);
        return lerp(t4, t5, t);
    }

    public static double normalizeYaw(double a, double b) {
        double diff = a - b;
        if (!(diff > (double)180.0F) && !(diff < (double)-180.0F)) {
            return b;
        } else {
            diff = Math.copySign((double)360.0F - Math.abs(diff), diff);
            return a + diff;
        }
    }

    public static double envelope(double x, double duration, double fades) {
        return envelope(x, (double)0.0F, fades, duration - fades, duration);
    }

    public static double envelope(double x, double lowIn, double lowOut, double highIn, double highOut) {
        if (!(x < lowIn) && !(x > highOut)) {
            if (x < lowOut) {
                return (x - lowIn) / (lowOut - lowIn);
            } else {
                return x > highIn ? (double)1.0F - (x - highIn) / (highOut - highIn) : (double)1.0F;
            }
        } else {
            return (double)0.0F;
        }
    }
}

