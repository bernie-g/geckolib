package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.model.entity.ExampleEntityModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {

	private int currentTick = -1;

	public ExampleGeoRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ExampleEntityModel());
	}

	@Override
	public RenderType getRenderType(PoseStack poseStack, GeoExampleEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight) {
		return RenderType.entityCutout(getTextureLocation(animatable));
	}

	@Override
	public void actuallyRender(PoseStack poseStack, GeoExampleEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (currentTick < 0 || currentTick != animatable.tickCount) {
			this.currentTick = animatable.tickCount;
			if (model.getBone("leftear").isPresent()) {
				animatable.getCommandSenderWorld().addParticle(ParticleTypes.PORTAL,
						model.getBone("leftear").get().getWorldPosition().x,
						model.getBone("leftear").get().getWorldPosition().y,
						model.getBone("leftear").get().getWorldPosition().z,
						(animatable.getRandom().nextDouble() - 0.5D), -animatable.getRandom().nextDouble(),
						(animatable.getRandom().nextDouble() - 0.5D));
			}
		}
		super.actuallyRender(poseStack, animatable, model, type, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
