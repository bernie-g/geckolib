package software.bernie.geckolib3.renderers.geo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

public interface IGeoRenderer<T> {
	VertexConsumerProvider getCurrentRTB();

	GeoModelProvider getGeoModelProvider();

	Identifier getTextureLocation(T animatable);

	default void render(GeoModel model, T animatable, float partialTick, RenderLayer type, MatrixStack poseStack,
			@Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		setCurrentRTB(bufferSource);
		renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green,
				blue, alpha);

		if (bufferSource != null)
			buffer = bufferSource.getBuffer(type);

		renderLate(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green,
				blue, alpha);
		// Render all top level bones
		for (GeoBone group : model.topLevelBones) {
			renderRecursively(group, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
		// Since we rendered at least once at this point, let's set the cycle to
		// repeated
		setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
	}

	default void renderRecursively(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.push();
		RenderUtils.prepMatrixForBone(poseStack, bone);
		renderCubesOfBone(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		renderChildBones(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.pop();
	}

	default void renderCubesOfBone(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isHidden())
			return;

		for (GeoCube cube : bone.childCubes) {
			if (!bone.cubesAreHidden()) {
				poseStack.push();
				renderCube(cube, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
				poseStack.pop();
			}
		}
	}

	default void renderChildBones(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.childBonesAreHiddenToo())
			return;

		for (GeoBone childBone : bone.childBones) {
			renderRecursively(childBone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	default void renderCube(GeoCube cube, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		RenderUtils.translateToPivotPoint(poseStack, cube);
		RenderUtils.rotateMatrixAroundCube(poseStack, cube);
		RenderUtils.translateAwayFromPivotPoint(poseStack, cube);
		Matrix3f normalisedPoseState = poseStack.peek().getNormalMatrix();
		Matrix4f poseState = poseStack.peek().getPositionMatrix();

		for (GeoQuad quad : cube.quads) {
			if (quad == null)
				continue;

			Vec3f normal = quad.normal.copy();

			normal.transform(normalisedPoseState);

			/*
			 * Fix shading dark shading for flat cubes + compatibility wish Optifine shaders
			 */
			if ((cube.size.getY() == 0 || cube.size.getZ() == 0) && normal.getX() < 0)
				normal.multiplyComponentwise(-1, 1, 1);

			if ((cube.size.getX() == 0 || cube.size.getZ() == 0) && normal.getY() < 0)
				normal.multiplyComponentwise(1, -1, 1);

			if ((cube.size.getX() == 0 || cube.size.getY() == 0) && normal.getZ() < 0)
				normal.multiplyComponentwise(1, 1, -1);

			createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	default void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vec3f normal, VertexConsumer buffer,
			int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		for (GeoVertex vertex : quad.vertices) {
			Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1);

			vector4f.transform(poseState);
			buffer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.textureU,
					vertex.textureV, packedOverlay, packedLight, normal.getX(), normal.getY(), normal.getZ());
		}
	}

	default void renderEarly(T animatable, MatrixStack poseStack, float partialTick,
			@Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
			float width = getWidthScale(animatable);
			float height = getHeightScale(animatable);

			poseStack.scale(width, height, width);
		}
	}

	default void renderLate(T animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource,
			VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
			float alpha) {
	}

	default RenderLayer getRenderType(T animatable, float partialTick, MatrixStack poseStack,
			@Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight,
			Identifier texture) {
		return RenderLayer.getEntityCutout(texture);
	}

	default Color getRenderColor(T animatable, float partialTick, MatrixStack poseStack,
			@Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight) {
		return Color.WHITE;
	}

	default int getInstanceId(T animatable) {
		return animatable.hashCode();
	}

	default void setCurrentModelRenderCycle(IRenderCycle cycle) {
	}

	@Nonnull
	default IRenderCycle getCurrentModelRenderCycle() {
		return EModelRenderCycle.INITIAL;
	}

	default void setCurrentRTB(VertexConsumerProvider bufferSource) {

	}

	default float getWidthScale(T animatable) {
		return 1F;
	}

	default float getHeightScale(T entity) {
		return 1F;
	}

	/**
	 * Use {@link IGeoRenderer#getInstanceId(Object)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	default Integer getUniqueID(T animatable) {
		return getInstanceId(animatable);
	}

	/**
	 * Use {@link RenderUtils#prepMatrixForBone(PoseStack, GeoBone)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	default void preparePositionRotationScale(GeoBone bone, MatrixStack poseStack) {
		RenderUtils.prepMatrixForBone(poseStack, bone);
	}
}
