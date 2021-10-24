package software.bernie.geckolib3.renderers.geo;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public abstract class GeoLayerRenderer<T extends Entity & IAnimatable> {
	private final IGeoRenderer<T> entityRenderer;

	public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn) {
		this.entityRenderer = entityRendererIn;
	}

	protected void renderCopyModel(GeoModelProvider<T> modelParentIn, Identifier textureLocationIn,
			MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entityIn, float limbSwing,
			float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red,
			float green, float blue) {
		if (!entityIn.isInvisible()) {
			this.renderModel(modelParentIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn,
					partialTicks, red, green, blue);
		}
	}

	protected void renderModel(GeoModelProvider<T> modelProviderIn, Identifier textureLocationIn,
			MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entityIn,
			float partialTicks, float red, float green, float blue) {
		GeoModel model = modelProviderIn.getModel(modelProviderIn.getModelLocation(entityIn));
		RenderLayer renderType = getRenderType(textureLocationIn);
		VertexConsumer ivertexbuilder = bufferIn.getBuffer(renderType);
		this.getRenderer().render(model, entityIn, partialTicks, renderType, matrixStackIn, bufferIn, ivertexbuilder,
				packedLightIn, LivingEntityRenderer.getOverlay((LivingEntity) entityIn, 0.0F), red, green, blue, 1.0F);
	}

	public RenderLayer getRenderType(Identifier textureLocation) {
		return RenderLayer.getEntityCutout(textureLocation);
	}
	
	@SuppressWarnings("unchecked")
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