package software.bernie.geckolib.util;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/**
 * Helper class for miscellaneous functions that don't fit into the other util classes
 */
public final class MiscUtil {
    /**
     * Converts a {@link Direction} to a rotational float for rotation purposes
     */
    public static float getDirectionAngle(Direction direction) {
        return switch(direction) {
            case SOUTH -> 90f;
            case NORTH -> 270f;
            case EAST -> 180f;
            default -> 0f;
        };
    }

    /**
     * Return whether the two floating point values should be considered numerically equal
     * <p>
     * This is important because of the way floating point values work, there may not necessarily be
     * 1:1 equality between two functionally equal floating point values
     */
    public static boolean areFloatsEqual(double a, double b) {
        return Math.abs(a - b) < Mth.EPSILON;
    }

    /**
     * Special helper function for lerping yaw.
     * <p>
     * This exists because yaw in Minecraft handles its yaw a bit strangely; and can cause incorrect results if lerped without accounting for special cases
     */
    public static double lerpYaw(double delta, double start, double end) {
        start = Mth.wrapDegrees(start);
        end = Mth.wrapDegrees(end);
        double diff = start - end;
        end = diff > 180 || diff < -180 ? start + Math.copySign(360 - Math.abs(diff), diff) : end;

        return Mth.lerp(delta, start, end);
    }

    private MiscUtil() {}
}
