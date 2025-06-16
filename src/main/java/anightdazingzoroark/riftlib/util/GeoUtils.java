package anightdazingzoroark.riftlib.util;

import net.minecraft.client.model.ModelRenderer;
import anightdazingzoroark.riftlib.core.processor.IBone;

public class GeoUtils
{
	public static void copyRotations(ModelRenderer from, IBone to)
	{
		to.setRotationX(-from.rotateAngleX);
		to.setRotationY(-from.rotateAngleY);
		to.setRotationZ(from.rotateAngleZ);
	}
}
