package software.bernie.geckolib3.renderers.geo;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.util.RenderUtils;

public interface IGeoRenderer<T> {
	default void render(GeoModel model, T animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn,
			@Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		renderEarly(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, alpha);

		if (renderTypeBuffer != null) {
			vertexBuilder = renderTypeBuffer.getBuffer(type);
		}
		renderLate(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, alpha);
		// Render all top level bones
		for (GeoBone group : model.topLevelBones) {
			renderRecursively(group, matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue,
					alpha);
		}
	}

	default void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		stack.pushPose();
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.rotate(bone, stack);
		RenderUtils.scale(bone, stack);
		RenderUtils.moveBackFromPivot(bone, stack);

		if (!bone.isHidden()) {
			for (GeoCube cube : bone.childCubes) {
				stack.pushPose();
				if (!bone.cubesAreHidden()) {
					renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				}
				stack.popPose();
			}
		}
		if (!bone.childBonesAreHiddenToo()) {
			for (GeoBone childBone : bone.childBones) {
				renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			}
		}

		stack.popPose();
	}

	default void renderCube(GeoCube cube, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		RenderUtils.moveToPivot(cube, stack);
		RenderUtils.rotate(cube, stack);
		RenderUtils.moveBackFromPivot(cube, stack);
		Matrix3f matrix3f = stack.last().normal();
		Matrix4f matrix4f = stack.last().pose();

		for (GeoQuad quad : cube.quads) {
			if (quad == null) {
				continue;
			}
			Vector3f normal = quad.normal.copy();
			normal.transform(matrix3f);

			/*
			 * Fix shading dark shading for flat cubes + compatibility wish Optifine shaders
			 */
			if ((cube.size.y() == 0 || cube.size.z() == 0) && normal.x() < 0) {
				normal.mul(-1, 1, 1);
			}
			if ((cube.size.x() == 0 || cube.size.z() == 0) && normal.y() < 0) {
				normal.mul(1, -1, 1);
			}
			if ((cube.size.x() == 0 || cube.size.y() == 0) && normal.z() < 0) {
				normal.mul(1, 1, -1);
			}

			for (GeoVertex vertex : quad.vertices) {
				Vector4f vector4f = new Vector4f(vertex.position.x(), vertex.position.y(), vertex.position.z(),
						1.0F);
				vector4f.transform(matrix4f);
				bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha,
						vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, normal.x(), normal.y(),
						normal.z());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	GeoModelProvider getGeoModelProvider();

	ResourceLocation getTextureLocation(T instance);

	default void renderEarly(T animatable, MatrixStack stackIn, float ticks,
			@Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float partialTicks) {
	}

	default void renderLate(T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer,
			IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
	}

	default RenderType getRenderType(T animatable, float partialTicks, MatrixStack stack,
			@Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityCutout(textureLocation);
	}

	default Color getRenderColor(T animatable, float partialTicks, MatrixStack stack,
			@Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn) {
		return Color.ofRGBA(255, 255, 255, 255);
	}

	default Integer getUniqueID(T animatable) {
		return animatable.hashCode();
	}
}
