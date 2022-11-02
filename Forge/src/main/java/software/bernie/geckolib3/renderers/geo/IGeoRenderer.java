package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.*;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IGeoRenderer<T> {
	MultiBufferSource getCurrentRTB();

	GeoModelProvider getGeoModelProvider();

	ResourceLocation getTextureLocation(T animatable);

	default void render(GeoModel model, T animatable, float partialTick, RenderType type, PoseStack poseStack,
			@Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		setCurrentRTB(bufferSource);
		renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlay, red, green, blue, alpha);

		if (bufferSource != null)
			buffer = bufferSource.getBuffer(type);

		renderLate(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlay, red, green, blue, alpha);
		// Render all top level bones
		for (GeoBone group : model.topLevelBones) {
			renderRecursively(group, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
					alpha);
		}
		// Since we rendered at least once at this point, let's set the cycle to
		// repeated
		setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
	}

	default void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		RenderUtils.prepMatrixForBone(poseStack, bone);
		renderCubesOfBone(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		renderChildBones(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}

	default void renderCubesOfBone(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isHidden())
			return;

		for (GeoCube cube : bone.childCubes) {
			if (!bone.cubesAreHidden()) {
				poseStack.pushPose();
				renderCube(cube, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
				poseStack.popPose();
			}
		}
	}

	default void renderChildBones(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.childBonesAreHiddenToo())
			return;

		for (GeoBone childBone : bone.childBones) {
			renderRecursively(childBone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	default void renderCube(GeoCube cube, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		RenderUtils.translateToPivotPoint(poseStack, cube);
		RenderUtils.rotateMatrixAroundCube(poseStack, cube);
		RenderUtils.translateAwayFromPivotPoint(poseStack, cube);
		Matrix3f normalisedPoseState = poseStack.last().normal();
		Matrix4f poseState = poseStack.last().pose();

		for (GeoQuad quad : cube.quads) {
			if (quad == null)
				continue;

			Vector3f normal = quad.normal.copy();

			normal.transform(normalisedPoseState);

			/*
			 * Fix shading dark shading for flat cubes + compatibility wish Optifine shaders
			 */
			if ((cube.size.y() == 0 || cube.size.z() == 0) && normal.x() < 0)
				normal.mul(-1, 1, 1);

			if ((cube.size.x() == 0 || cube.size.z() == 0) && normal.y() < 0)
				normal.mul(1, -1, 1);

			if ((cube.size.x() == 0 || cube.size.y() == 0) && normal.z() < 0)
				normal.mul(1, 1, -1);

			createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	default void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
			int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		for (GeoVertex vertex : quad.vertices) {
			Vector4f vector4f = new Vector4f(vertex.position.x(), vertex.position.y(), vertex.position.z(), 1);

			vector4f.transform(poseState);
			buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.textureU,
					vertex.textureV, packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
		}
	}

	default void renderEarly(T animatable, PoseStack poseStack, float partialTick,
			@Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
			float width = getWidthScale(animatable);
			float height = getHeightScale(animatable);

			poseStack.scale(width, height, width);
		}
	}

	default void renderLate(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
			VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
			float alpha) {}

	default RenderType getRenderType(T animatable, float partialTick, PoseStack poseStack,
			@Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight,
			ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

	default Color getRenderColor(T animatable, float partialTick, PoseStack poseStack,
			@Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight) {
		return Color.WHITE;
	}

	default int getInstanceId(T animatable) {
		return animatable.hashCode();
	}

	default void setCurrentModelRenderCycle(IRenderCycle cycle) {}

	@Nonnull
	default IRenderCycle getCurrentModelRenderCycle() {
		return EModelRenderCycle.INITIAL;
	}

	default void setCurrentRTB(MultiBufferSource bufferSource) {

	}

	default float getWidthScale(T animatable) {
		return 1F;
	}

	default float getHeightScale(T entity) {
		return 1F;
	}

	/**
	 * Use {@link RenderUtils#prepMatrixForBone(PoseStack, GeoBone)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	default void preparePositionRotationScale(GeoBone bone, PoseStack poseStack) {
		RenderUtils.prepMatrixForBone(poseStack, bone);
	}
}
