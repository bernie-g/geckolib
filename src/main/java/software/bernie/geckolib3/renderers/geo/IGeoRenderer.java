package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.*;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
import java.awt.*;

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
		stack.push();
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.rotate(bone, stack);
		RenderUtils.scale(bone, stack);
		RenderUtils.moveBackFromPivot(bone, stack);

		if (!bone.isHidden) {
			for (GeoCube cube : bone.childCubes) {
				stack.push();
				renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				stack.pop();
			}
			for (GeoBone childBone : bone.childBones) {
				renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			}
		}

		stack.pop();
	}

	default void renderCube(GeoCube cube, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		RenderUtils.moveToPivot(cube, stack);
		RenderUtils.rotate(cube, stack);
		RenderUtils.moveBackFromPivot(cube, stack);
		Matrix3f matrix3f = stack.getLast().getNormal();
		Matrix4f matrix4f = stack.getLast().getMatrix();

		for (GeoQuad quad : cube.quads) {
			if (quad == null) {
				continue;
			}
			Vector3f normal = quad.normal.copy();
			normal.transform(matrix3f);

			/*
			 * Fix shading dark shading for flat cubes + compatibility wish Optifine shaders
			 */
			if ((cube.size.getY() == 0 || cube.size.getZ() == 0) && normal.getX() < 0) {
				normal.mul(-1, 1, 1);
			}
			if ((cube.size.getX() == 0 || cube.size.getZ() == 0) && normal.getY() < 0) {
				normal.mul(1, -1, 1);
			}
			if ((cube.size.getX() == 0 || cube.size.getY() == 0) && normal.getZ() < 0) {
				normal.mul(1, 1, -1);
			}

			for (GeoVertex vertex : quad.vertices) {
				Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(),
						1.0F);
				vector4f.transform(matrix4f);
				bufferIn.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha,
						vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(),
						normal.getZ());
			}
		}
	}

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
		return RenderType.getEntityCutout(textureLocation);
	}

	default Color getRenderColor(T animatable, float partialTicks, MatrixStack stack,
			@Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn) {
		return new Color(255, 255, 255, 255);
	}

	default Integer getUniqueID(T animatable) {
		return animatable.hashCode();
	}
}
