package software.bernie.geckolib3.util;

import net.minecraft.client.model.ModelPart;
import software.bernie.geckolib3.core.processor.IBone;

public class GeoUtils {
	public static void copyRotations(ModelPart from, IBone to) {
		to.setRotationX(-from.pitch);
		to.setRotationY(-from.yaw);
		to.setRotationZ(from.roll);
	}
}
