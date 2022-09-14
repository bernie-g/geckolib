package software.bernie.geckolib3.util;

import net.minecraft.client.model.geom.ModelPart;
import software.bernie.geckolib3.core.processor.IBone;

public class GeoUtils {
	public static void copyRotations(ModelPart from, IBone to) {
		to.setRotationX(-from.xRot);
		to.setRotationY(-from.yRot);
		to.setRotationZ(from.zRot);
	}
}
