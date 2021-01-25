package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.util.AnimationUtils;

public abstract class GeoLayerRenderer<T extends Entity & IAnimatable>
{
	private final IGeoRenderer<T> entityRenderer;

	public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn)
	{
		this.entityRenderer = entityRendererIn;
	}

	protected static <T extends LivingEntity & IAnimatable> void renderCopyModel(GeoModelProvider<T> modelProviderIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float partialTicks, float red, float green, float blue)
	{
		if (!entityIn.isInvisible())
		{
			renderModel(modelProviderIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, partialTicks, red, green, blue);
		}
	}

	protected static <T extends LivingEntity & IAnimatable> void renderModel(GeoModelProvider<T> modelProviderIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float partialTicks, float red, float green, float blue)
	{
		GeoModel model = modelProviderIn.getModel(modelProviderIn.getModelLocation(entityIn));
		IGeoRenderer<T> renderer = (IGeoRenderer<T>)AnimationUtils.getRenderer(entityIn);
		RenderType renderType = getRenderType(textureLocationIn);
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(renderType);
		renderer.render(model, entityIn, partialTicks, renderType, matrixStackIn, bufferIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entityIn, 0.0F), red, green, blue, 1.0F);
	}

	public static RenderType getRenderType(ResourceLocation textureLocation)
	{
		return RenderType.getEntityCutout(textureLocation);
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
