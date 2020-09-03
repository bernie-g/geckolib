package software.bernie.geckolib.geo.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.geo.render.built.*;
import software.bernie.geckolib.model.IGeoModel;
import software.bernie.geckolib.util.RenderUtils;

import java.awt.*;

public abstract class GeoEntityRenderer<T extends Entity> extends EntityRenderer<T>
{
	private final IGeoModel modelProvider;

	protected GeoEntityRenderer(EntityRendererManager renderManager, IGeoModel modelProvider)
	{
		super(renderManager);
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		Minecraft.getInstance().textureManager.bindTexture(getEntityTexture(entityIn));
		GeoModel model = modelProvider.getModel();
		Color renderColor = getRenderColor(entityIn, partialTicks, stack, bufferIn, packedLightIn);
		RenderType renderType = getRenderType(entityIn, partialTicks, stack, bufferIn, packedLightIn, getEntityTexture(entityIn));
		stack.push();
		render(model, entityIn, entityYaw, partialTicks, stack, bufferIn.getBuffer(renderType), packedLightIn, OverlayTexture.NO_OVERLAY, renderColor.getRed(), renderColor.getBlue(), renderColor.getGreen(), renderColor.getAlpha());
		stack.pop();

		super.render(entityIn, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	private void render(GeoModel model, T entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		//Render all top level bones
		for (GeoBone group : model.topLevelBones)
		{
			renderRecursively(group, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}

	private void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.scale(bone, stack);
		RenderUtils.rotate(bone, stack);

		for(GeoCube cube : bone.childCubes)
		{
			renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}

		for(GeoBone childBone : bone.childBones)
		{
			renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}

		RenderUtils.moveBackFromPivot(bone, stack);
		RenderUtils.translate(bone, stack);
	}

	private void renderCube(GeoCube cube, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		RenderUtils.moveToPivot(cube, stack);
		RenderUtils.rotate(cube, stack);
		RenderUtils.moveBackFromPivot(cube, stack);

		for(GeoQuad quad : cube.quads)
		{
			for(GeoVertex vertex : quad.vertices)
			{
				bufferIn.addVertex(vertex.position.getX() / 16, vertex.position.getY() / 16, vertex.position.getZ() / 16, red, green, blue, alpha, vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());
			}
		}
	}

	protected RenderType getRenderType(T entity, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ResourceLocation textureLocation)
	{
		return RenderType.getEntityCutoutNoCull(textureLocation);
	}

	protected Color getRenderColor(T entity, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		return new Color(255, 255, 255, 255);
	}

	public IGeoModel getGeoModelProvider()
	{
		return this.modelProvider;
	}
}
