package software.bernie.geckolib.object;

import net.minecraft.util.Mth;
import software.bernie.geckolib.GeckoLibConstants;

/// Directional enum defining 90-degree increment rotations
public enum Rotation {
    NONE,
    CLOCKWISE_90,
    COUNTERCLOCKWISE_90,
    CLOCKWISE_180;

    /// Parse a Rotation from a value in degrees
    public static Rotation fromValue(int value) {
        try {
            while (value < 0) {
                value += 360;
            }

            return Rotation.values()[(value % 360) / 90];
        }
        catch (Exception e) {
            GeckoLibConstants.LOGGER.error("Invalid rotation degrees value, must be in 90-degree increments: {}", value);

            return fromValue(Mth.floor(Math.abs(value) / 90f) * 90);
        }
    }
}
