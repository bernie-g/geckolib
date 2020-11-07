package software.bernie.geckolib3.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

public class RenderUtils
{
	public static void moveToPivot(GeoCube cube, MatrixStack stack)
	{
		Vector3f pivot = cube.pivot;
		stack.translate(pivot.getX() / 16, pivot.getY() / 16, pivot.getZ() / 16);
	}

	public static void moveBackFromPivot(GeoCube cube, MatrixStack stack)
	{
		Vector3f pivot = cube.pivot;
		stack.translate(-pivot.getX() / 16, -pivot.getY() / 16, -pivot.getZ() / 16);
	}

	public static void moveToPivot(GeoBone bone, MatrixStack stack)
	{
		stack.translate(bone.rotationPointX / 16, bone.rotationPointY / 16, bone.rotationPointZ / 16);
	}


	public static void moveBackFromPivot(GeoBone bone, MatrixStack stack)
	{
		stack.translate(-bone.rotationPointX / 16, -bone.rotationPointY / 16, -bone.rotationPointZ / 16);
	}

	public static void scale(GeoBone bone, MatrixStack stack)
	{
		stack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translate(GeoBone bone, MatrixStack stack)
	{
		stack.translate(-bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16);
	}

	public static void rotate(GeoBone bone, MatrixStack stack)
	{
		if (bone.getRotationZ() != 0.0F)
		{
			stack.rotate(Vector3f.ZP.rotation(bone.getRotationZ()));
		}

		if (bone.getRotationY() != 0.0F)
		{
			stack.rotate(Vector3f.YP.rotation(bone.getRotationY()));
		}

		if (bone.getRotationX() != 0.0F)
		{
			stack.rotate(Vector3f.XP.rotation(bone.getRotationX()));
		}
	}

	public static void rotate(GeoCube bone, MatrixStack stack)
	{
		Vector3f rotation = bone.rotation;
		stack.rotate(new Quaternion(rotation.getX(), rotation.getY(), rotation.getZ(), false));
	}
}
