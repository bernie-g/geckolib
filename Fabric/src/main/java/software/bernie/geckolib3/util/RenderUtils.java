package software.bernie.geckolib3.util;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

public class RenderUtils {
	public static void moveToPivot(GeoCube cube, PoseStack stack) {
		Vector3f pivot = cube.pivot;
		stack.translate(pivot.x() / 16, pivot.y() / 16, pivot.z() / 16);
	}

	public static void moveBackFromPivot(GeoCube cube, PoseStack stack) {
		Vector3f pivot = cube.pivot;
		stack.translate(-pivot.x() / 16, -pivot.y() / 16, -pivot.z() / 16);
	}

	public static void moveToPivot(GeoBone bone, PoseStack stack) {
		stack.translate(bone.pivotX / 16, bone.pivotY / 16, bone.pivotZ / 16);
	}

	public static void moveBackFromPivot(GeoBone bone, PoseStack stack) {
		stack.translate(-bone.pivotX / 16, -bone.pivotY / 16, -bone.pivotZ / 16);
	}

	public static void scale(GeoBone bone, PoseStack stack) {
		stack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translate(GeoBone bone, PoseStack stack) {
		stack.translate(-bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16);
	}

	public static void rotate(GeoBone bone, PoseStack stack) {
		if (bone.getRotationZ() != 0.0F) {
			stack.mulPose(Axis.ZP.rotation(bone.getRotationZ()));
		}

		if (bone.getRotationY() != 0.0F) {
			stack.mulPose(Axis.YP.rotation(bone.getRotationY()));
		}

		if (bone.getRotationX() != 0.0F) {
			stack.mulPose(Axis.XP.rotation(bone.getRotationX()));
		}
	}

	public static void rotate(GeoCube bone, PoseStack stack) {
		Vector3f rotation = bone.rotation;

		stack.mulPose(new Quaternionf().rotationXYZ(0, 0, rotation.z()));
		stack.mulPose(new Quaternionf().rotationXYZ(0, rotation.y(), 0));
		stack.mulPose(new Quaternionf().rotationXYZ(rotation.x(), 0, 0));
	}
}
