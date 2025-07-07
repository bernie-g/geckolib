package anightdazingzoroark.riftlib;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

//wait what, theres a config file now???
public class RiftLibConfig {
    public static float HITBOX_DISPLACEMENT_TOLERANCE = 0.1f;
    public static float HITBOX_RESIZING_TOLERANCE = 0.1f;
    public static float RIDE_POS_DISPLACEMENT_TOLERANCE = 0.05f;

    public static void readConfig() {
        Configuration config = RiftLib.configMain;
        try {
            config.load();
            init(config);
        }
        catch (Exception e1) {
            RiftLib.LOGGER.log(Level.ERROR, "Problem loading config file!", e1);
        }
        finally {
            if (config.hasChanged()) config.save();
        }
    }

    public static void init(Configuration config) {
        HITBOX_DISPLACEMENT_TOLERANCE = config.getFloat("Hitbox Displacement Tolerance", "General", 0.1f, 0, Integer.MAX_VALUE, "Minimum total change in hitbox positions from animations");
        HITBOX_RESIZING_TOLERANCE = config.getFloat("Hitbox Resizing Tolerance", "General", 0.1f, 0, Integer.MAX_VALUE, "Minimum total change in hitbox size from animations");
        RIDE_POS_DISPLACEMENT_TOLERANCE = config.getFloat("Ride Position Displacement Tolerance", "General", 0.05f, 0, Integer.MAX_VALUE, "Minimum total change in ride positions from animations");
    }
}
