package software.bernie.geckolib3.util;

import net.minecraft.client.model.geom.ModelPart;
import software.bernie.geckolib3.core.animatable.model.GeoBone;

public class GeoUtils {
	public static void copyRotations(ModelPart from, GeoBone to) {
		to.setRotationX(-from.xRot);
		to.setRotationY(-from.yRot);
		to.setRotationZ(from.zRot);
	}
}
