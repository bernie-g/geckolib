package software.bernie.geckolib3.renderers.geo;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public abstract class GeoLayerRenderer<T extends Entity & IAnimatable> {
	protected final IGeoRenderer<T> entityRenderer;

	public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn) {
		this.entityRenderer = entityRendererIn;
	}

	protected void renderCopyModel(GeoModelProvider<T> modelProvider,
			Identifier texture, MatrixStack poseStack, VertexConsumerProvider bufferSource,
			int packedLight, T animatable, float partialTick, float red, float green, float blue) {
		if (!animatable.isInvisible()) {
			renderModel(modelProvider, texture, poseStack, bufferSource, packedLight, animatable,
					partialTick, red, green, blue);
		}
	}

	protected void renderModel(GeoModelProvider<T> modelProvider,
			Identifier texture, MatrixStack poseStack, VertexConsumerProvider bufferSource,
			int packedLight, T animatable, float partialTick, float red, float green, float blue) {
		if (animatable instanceof LivingEntity entity) {
			RenderLayer renderType = getRenderType(texture);

			getRenderer().render(modelProvider.getModel(modelProvider.getModelLocation(animatable)),
					animatable, partialTick, renderType, poseStack, bufferSource, bufferSource.getBuffer(renderType),
					packedLight, LivingEntityRenderer.getOverlay(entity, 0), red, green, blue, 1);
		}
	}

	public RenderLayer getRenderType(Identifier textureLocation) {
		return RenderLayer.getEntityCutout(textureLocation);
	}
	
	public GeoModelProvider<T> getEntityModel() {
		return this.entityRenderer.getGeoModelProvider();
	}

	public IGeoRenderer<T> getRenderer() {
		return this.entityRenderer;
	}

	protected Identifier getEntityTexture(T entityIn) {
		return this.entityRenderer.getTextureLocation(entityIn);
	}

	public abstract void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
			T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
			float netHeadYaw, float headPitch);
}