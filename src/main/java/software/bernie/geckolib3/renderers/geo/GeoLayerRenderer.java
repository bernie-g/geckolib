package software.bernie.geckolib3.renderers.geo;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public abstract class GeoLayerRenderer<T extends EntityLivingBase & IAnimatable> implements LayerRenderer<T> {
	protected final IGeoRenderer<T> entityRenderer;

	public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn) {
		this.entityRenderer = entityRendererIn;
	}

	protected static <T extends EntityLivingBase> void renderCopyCutoutModel(ModelBase modelParentIn, ModelBase modelIn,
			ResourceLocation textureLocationIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue) {
		if (!entityIn.isInvisible()) {
			modelParentIn.setModelAttributes(modelIn);
			modelIn.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
			modelIn.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 1 / 16F, entityIn);
			renderCutoutModel(modelIn, textureLocationIn, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
					headPitch, 1 / 16F, red, green, blue);
		}
	}

	protected static <T extends EntityLivingBase> void renderCutoutModel(ModelBase modelIn,
			ResourceLocation textureLocationIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch, float scale, float red, float green, float blue) {
		GlStateManager.color(red, green, blue, 1f);
		modelIn.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	}

	@Override
	public void doRenderLayer(T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
			float netHeadYaw, float headPitch, float scaleIn) {
	}

	public IGeoRenderer<T> getRenderer() {
		return this.entityRenderer;
	}

	@SuppressWarnings("unchecked")
	public GeoModelProvider<T> getEntityModel() {
		return this.entityRenderer.getGeoModelProvider();
	}

	protected ResourceLocation getEntityTexture(T entityIn) {
		return this.entityRenderer.getTextureLocation(entityIn);
	}

	public abstract void render(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, Color renderColor);
}
