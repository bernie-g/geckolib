package software.bernie.geckolib3.renderer.geo;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public abstract class GeoLayerRenderer<T extends Entity & IAnimatable> {
    private final IGeoRenderer<T> entityRenderer;

    public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn) {
        this.entityRenderer = entityRendererIn;
    }

	protected static <T extends LivingEntity> void renderCopyModel(EntityModel<T> modelParentIn, EntityModel<T> modelIn, Identifier textureLocationIn, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue)
	{
		if (!entityIn.isInvisible())
		{
			modelParentIn.copyStateTo(modelIn);
			modelIn.animateModel(entityIn, limbSwing, limbSwingAmount, partialTicks);
			modelIn.setAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			renderModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
		}
	}

	protected static <T extends LivingEntity> void renderModel(EntityModel<T> modelIn, Identifier textureLocationIn, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entityIn, float red, float green, float blue)
	{
		RenderLayer renderType = getRenderType(textureLocationIn);
		VertexConsumer ivertexbuilder = bufferIn.getBuffer(renderType);
		modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlay(entityIn, 0.0F), red, green, blue, 1.0F);
	}

	public static RenderLayer getRenderType(Identifier textureLocation)
	{
		return RenderLayer.getEntityCutout(textureLocation);
	}

    public GeoModelProvider getEntityModel() {
        return this.entityRenderer.getGeoModelProvider();
    }

    protected Identifier getEntityTexture(T entityIn) {
        return this.entityRenderer.getTextureLocation(entityIn);
    }

    public abstract void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);
}