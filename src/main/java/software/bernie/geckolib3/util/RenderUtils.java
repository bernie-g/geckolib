package software.bernie.geckolib3.util;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

public class RenderUtils {
    public static void moveToPivot(GeoCube cube, MatrixStack stack) {
        Vector3f pivot = cube.pivot;
        stack.translate(pivot.getX() / 16, pivot.getY() / 16, pivot.getZ() / 16);
    }

    public static void moveBackFromPivot(GeoCube cube, MatrixStack stack) {
        Vector3f pivot = cube.pivot;
        stack.translate(-pivot.getX() / 16, -pivot.getY() / 16, -pivot.getZ() / 16);
    }

    public static void moveToPivot(GeoBone bone, MatrixStack stack) {
        stack.translate(bone.pivotX / 16, bone.pivotY / 16, bone.pivotZ / 16);
    }

    public static void moveBackFromPivot(GeoBone bone, MatrixStack stack) {
        stack.translate(-bone.pivotX / 16, -bone.pivotY / 16, -bone.pivotZ / 16);
    }

    public static void scale(GeoBone bone, MatrixStack stack) {
        stack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void translate(GeoBone bone, MatrixStack stack) {
        stack.translate(-bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16);
    }

    public static void rotate(GeoBone bone, MatrixStack stack) {
        if (bone.getRotationZ() != 0.0F) {
            stack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(bone.getRotationZ()));
        }

        if (bone.getRotationY() != 0.0F) {
            stack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(bone.getRotationY()));
        }

        if (bone.getRotationX() != 0.0F) {
            stack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(bone.getRotationX()));
        }
    }

	public static void rotate(GeoCube bone, MatrixStack stack)
	{
		Vector3f rotation = bone.rotation;

		stack.multiply(new Quaternion(0, 0, rotation.getZ(), false));
		stack.multiply(new Quaternion(0, rotation.getY(), 0, false));
		stack.multiply(new Quaternion(rotation.getX(), 0, 0, false));
	}
}
