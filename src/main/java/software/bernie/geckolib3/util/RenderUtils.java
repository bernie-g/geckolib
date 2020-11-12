package software.bernie.geckolib3.util;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Quaternion;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

import javax.vecmath.Vector3f;

public class RenderUtils
{
	public static void moveToPivot(GeoCube cube)
	{
		Vector3f pivot = cube.pivot;
		GlStateManager.translate(pivot.getX() / 16, pivot.getY() / 16, pivot.getZ() / 16);
	}

	public static void moveBackFromPivot(GeoCube cube)
	{
		Vector3f pivot = cube.pivot;
		GlStateManager.translate(-pivot.getX() / 16, -pivot.getY() / 16, -pivot.getZ() / 16);
	}

	public static void moveToPivot(GeoBone bone)
	{
		GlStateManager.translate(bone.rotationPointX / 16, bone.rotationPointY / 16, bone.rotationPointZ / 16);
	}


	public static void moveBackFromPivot(GeoBone bone)
	{
		GlStateManager.translate(-bone.rotationPointX / 16, -bone.rotationPointY / 16, -bone.rotationPointZ / 16);
	}

	public static void scale(GeoBone bone)
	{
		GlStateManager.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translate(GeoBone bone)
	{
		GlStateManager.translate(-bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16);
	}

	public static void rotate(GeoBone bone)
	{
		if (bone.getRotationZ() != 0.0F)
		{
			GlStateManager.rotate(bone.getRotationZ(), 0, 0, 1);
		}

		if (bone.getRotationY() != 0.0F)
		{
			GlStateManager.rotate(bone.getRotationY(), 0, 1, 0);
		}

		if (bone.getRotationX() != 0.0F)
		{
			GlStateManager.rotate(bone.getRotationX(), 1, 0, 0);
		}
	}

	public static void rotate(GeoCube bone)
	{
		Vector3f rotation = bone.rotation;
		Quaternion quat = new Quaternion(rotation.getX(), rotation.getY(), rotation.getZ(), 0);

		GlStateManager.rotate(quat);
	}
}
