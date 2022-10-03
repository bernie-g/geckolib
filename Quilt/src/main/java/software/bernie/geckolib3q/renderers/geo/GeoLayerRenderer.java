package software.bernie.geckolib3q.renderers.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.model.provider.GeoModelProvider;

public abstract class GeoLayerRenderer<T extends Entity & IAnimatable> {
	protected final IGeoRenderer<T> entityRenderer;

	public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn) {
		this.entityRenderer = entityRendererIn;
	}

	protected void renderCopyModel(GeoModelProvider<T> modelParentIn, ResourceLocation textureLocationIn,
			PoseStack PoseStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityIn, float limbSwing,
			float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red,
			float green, float blue) {
		if (!entityIn.isInvisible()) {
			this.renderModel(modelParentIn, textureLocationIn, PoseStackIn, bufferIn, packedLightIn, entityIn,
					partialTicks, red, green, blue);
		}
	}

	protected void renderModel(GeoModelProvider<T> modelProviderIn, ResourceLocation textureLocationIn,
			PoseStack PoseStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityIn, float partialTicks,
			float red, float green, float blue) {
		GeoModel model = modelProviderIn.getModel(modelProviderIn.getModelResource(entityIn));
		RenderType renderType = getRenderType(textureLocationIn);
		VertexConsumer ivertexbuilder = bufferIn.getBuffer(renderType);
		this.getRenderer().render(model, entityIn, partialTicks, renderType, PoseStackIn, bufferIn, ivertexbuilder,
				packedLightIn, LivingEntityRenderer.getOverlayCoords((LivingEntity) entityIn, 0.0F), red, green, blue,
				1.0F);
	}

	public RenderType getRenderType(ResourceLocation textureLocation) {
		return RenderType.entityCutout(textureLocation);
	}

	public GeoModelProvider<T> getEntityModel() {
		return this.entityRenderer.getGeoModelProvider();
	}

	public IGeoRenderer<T> getRenderer() {
		return this.entityRenderer;
	}

	protected ResourceLocation getEntityTexture(T entityIn) {
		return this.entityRenderer.getTextureLocation(entityIn);
	}

	public ResourceLocation getTextureResource(T entity) {
		return this.entityRenderer.getTextureResource(entity);
	}

	public abstract void render(PoseStack PoseStackIn, MultiBufferSource bufferIn, int packedLightIn,
			T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
			float netHeadYaw, float headPitch);
}