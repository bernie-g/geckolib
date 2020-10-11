package software.bernie.geckolib.util;

import net.minecraft.client.renderer.model.ModelRenderer;
import software.bernie.geckolib.core.processor.IBone;

public class GeoUtils
{
	public static void copyRotations(ModelRenderer from, IBone to)
	{
		to.setRotationX(-from.rotateAngleX);
		to.setRotationY(-from.rotateAngleY);
		to.setRotationZ(from.rotateAngleZ);
	}
}
