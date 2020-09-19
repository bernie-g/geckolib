package software.bernie.geckolib.geo.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.geo.render.built.*;
import software.bernie.geckolib.model.provider.GeoModelProvider;
import software.bernie.geckolib.util.RenderUtils;

import java.awt.*;

public interface IGeoRenderer<T>
{
	default void render(GeoModel model, T entity, float partialTicks, RenderType type, MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		renderEarly(entity, matrixStackIn, partialTicks, renderTypeBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		IVertexBuilder buffer = renderTypeBuffer.getBuffer(type);
		renderLate(entity, matrixStackIn, partialTicks, renderTypeBuffer, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		//Render all top level bones
		for (GeoBone group : model.topLevelBones)
		{
			renderRecursively(group, matrixStackIn, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}

	default void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		stack.push();
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.rotate(bone, stack);
		RenderUtils.scale(bone, stack);
		RenderUtils.moveBackFromPivot(bone, stack);

		for (GeoCube cube : bone.childCubes)
		{
			renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}

		for (GeoBone childBone : bone.childBones)
		{
			renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
		stack.pop();
	}

	default void renderCube(GeoCube cube, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		RenderUtils.moveToPivot(cube, stack);
		RenderUtils.rotate(cube, stack);
		RenderUtils.moveBackFromPivot(cube, stack);
		Matrix3f matrix3f = stack.getLast().getNormal();
		Matrix4f matrix4f = stack.getLast().getMatrix();

		for (GeoQuad quad : cube.quads)
		{
			Vector3f normal = quad.normal.copy();
			normal.transform(matrix3f);

			if(normal.getX() < 0)
			{
				normal.mul(-1, 1, 1);
			}
			if(normal.getY() < 0)
			{
				normal.mul(1, -1, 1);
			}
			if(normal.getZ() < 0)
			{
				normal.mul(1, 1, -1);
			}

			for (GeoVertex vertex : quad.vertices)
			{
				Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1.0F);
				vector4f.transform(matrix4f);
				bufferIn.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(), normal.getZ());
			}
		}
	}

	GeoModelProvider getGeoModelProvider();
	ResourceLocation getTextureLocation(T instance);

	default void renderEarly(T instance, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks)
	{
	}

	default void renderLate(T instance, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks)
	{
	}

	default RenderType getRenderType(T instance, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ResourceLocation textureLocation)
	{
		return RenderType.getEntityTranslucent(textureLocation);
	}

	default Color getRenderColor(T instance, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		return new Color(255, 255, 255, 255);
	}


}
