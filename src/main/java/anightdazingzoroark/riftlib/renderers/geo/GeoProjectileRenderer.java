package anightdazingzoroark.riftlib.renderers.geo;

import java.util.Collections;

import anightdazingzoroark.riftlib.projectile.RiftLibProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.IAnimatableModel;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.event.predicate.AnimationEvent;
import anightdazingzoroark.riftlib.core.util.Color;
import anightdazingzoroark.riftlib.geo.render.built.GeoModel;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.model.provider.GeoModelProvider;
import anightdazingzoroark.riftlib.model.provider.data.EntityModelData;
import anightdazingzoroark.riftlib.util.AnimationUtils;

@SuppressWarnings("unchecked")
public class GeoProjectileRenderer<T extends RiftLibProjectile & IAnimatable> extends Render<T> implements IGeoRenderer<T> {
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof RiftLibProjectile) {
				return (IAnimatableModel<Object>) AnimationUtils.getGeoModelForEntity((RiftLibProjectile) object);
			}
			return null;
		});
	}

	private final AnimatedGeoModel<T> modelProvider;

	public GeoProjectileRenderer(RenderManager renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);
		this.modelProvider = modelProvider;
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.pushMatrix();
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entity));
		GlStateManager.translate(x, y, z);
		if (entity.canRotateToAimDirection()) {
			GlStateManager.rotate(
					entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F,
					1.0F, 0.0F);
			GlStateManager.rotate(
					entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F,
					1.0F);
		}

		float lastLimbDistance = 0.0F;
		float limbSwing = 0.0F;
		EntityModelData entityModelData = new EntityModelData();
		AnimationEvent<T> predicate = new AnimationEvent<T>(entity, limbSwing, lastLimbDistance, partialTicks,
				!(lastLimbDistance > -0.15F && lastLimbDistance < 0.15F), Collections.singletonList(entityModelData));
		if (modelProvider instanceof IAnimatableModel) {
			((IAnimatableModel<T>) modelProvider).setLivingAnimations(entity, this.getUniqueID(entity), predicate);
		}
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(getTextureLocation(entity));
		Color renderColor = getRenderColor(entity, partialTicks);

		if (!entity.isInvisibleToPlayer(Minecraft.getMinecraft().player))
			render(model, entity, partialTicks, (float) renderColor.getRed() / 255f,
					(float) renderColor.getBlue() / 255f, (float) renderColor.getGreen() / 255f,
					(float) renderColor.getAlpha() / 255);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@Override
	public ResourceLocation getEntityTexture(T instance) {
		return getTextureLocation(instance);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return animatable.getUniqueID().hashCode();
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

}
