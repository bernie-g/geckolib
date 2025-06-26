package anightdazingzoroark.riftlib.molang.utils;

public enum Interpolation {
    LINEAR("linear") {
        public float interpolate(float a, float b, float x) {
            return Interpolations.lerp(a, b, x);
        }
    },
    QUAD_IN("quad_in") {
        public float interpolate(float a, float b, float x) {
            return a + (b - a) * x * x;
        }
    },
    QUAD_OUT("quad_out") {
        public float interpolate(float a, float b, float x) {
            return a - (b - a) * x * (x - 2.0F);
        }
    },
    QUAD_INOUT("quad_inout") {
        public float interpolate(float a, float b, float x) {
            x *= 2.0F;
            if (x < 1.0F) {
                return a + (b - a) / 2.0F * x * x;
            } else {
                --x;
                return a - (b - a) / 2.0F * (x * (x - 2.0F) - 1.0F);
            }
        }
    },
    CUBIC_IN("cubic_in") {
        public float interpolate(float a, float b, float x) {
            return a + (b - a) * x * x * x;
        }
    },
    CUBIC_OUT("cubic_out") {
        public float interpolate(float a, float b, float x) {
            --x;
            return a + (b - a) * (x * x * x + 1.0F);
        }
    },
    CUBIC_INOUT("cubic_inout") {
        public float interpolate(float a, float b, float x) {
            x *= 2.0F;
            if (x < 1.0F) {
                return a + (b - a) / 2.0F * x * x * x;
            } else {
                x -= 2.0F;
                return a + (b - a) / 2.0F * (x * x * x + 2.0F);
            }
        }
    },
    EXP_IN("exp_in") {
        public float interpolate(float a, float b, float x) {
            return a + (b - a) * (float)Math.pow((double)2.0F, (double)(10.0F * (x - 1.0F)));
        }
    },
    EXP_OUT("exp_out") {
        public float interpolate(float a, float b, float x) {
            return a + (b - a) * (float)(-Math.pow((double)2.0F, (double)(-10.0F * x)) + (double)1.0F);
        }
    },
    EXP_INOUT("exp_inout") {
        public float interpolate(float a, float b, float x) {
            if (x == 0.0F) {
                return a;
            } else if (x == 1.0F) {
                return b;
            } else {
                x *= 2.0F;
                if (x < 1.0F) {
                    return a + (b - a) / 2.0F * (float)Math.pow((double)2.0F, (double)(10.0F * (x - 1.0F)));
                } else {
                    --x;
                    return a + (b - a) / 2.0F * (float)(-Math.pow((double)2.0F, (double)(-10.0F * x)) + (double)2.0F);
                }
            }
        }
    };

    public final String key;

    private Interpolation(String key) {
        this.key = key;
    }

    public abstract float interpolate(float var1, float var2, float var3);

    public String getName() {
        return "mclib.interpolations." + this.key;
    }
}
