package software.bernie.geckolib.renderers.geo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.IAnimatable;
import software.bernie.geckolib.model.provider.GeoModelProvider;

public abstract class GeoLayerRenderer<T extends Entity & IAnimatable>
{
	private final IGeoRenderer<T> entityRenderer;

	public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn)
	{
		this.entityRenderer = entityRendererIn;
	}

	protected static <T extends LivingEntity> void renderCopyCutoutModel(EntityModel<T> modelParentIn, EntityModel<T> modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue)
	{
		if (!entityIn.isInvisible())
		{
			modelParentIn.copyModelAttributesTo(modelIn);
			modelIn.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
			modelIn.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			renderCutoutModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
		}
	}

	protected static <T extends LivingEntity> void renderCutoutModel(EntityModel<T> modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float red, float green, float blue)
	{
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(textureLocationIn));
		modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entityIn, 0.0F), red, green, blue, 1.0F);
	}

	public GeoModelProvider getEntityModel()
	{
		return this.entityRenderer.getGeoModelProvider();
	}

	protected ResourceLocation getEntityTexture(T entityIn)
	{
		return this.entityRenderer.getTextureLocation(entityIn);
	}

	public abstract void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);
}
