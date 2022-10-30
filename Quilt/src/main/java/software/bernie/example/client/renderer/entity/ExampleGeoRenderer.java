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
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.renderers.geo.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {

	private int currentTick = -1;

	public ExampleGeoRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ExampleEntityModel());
	}

	@Override
	public RenderType getRenderType(GeoExampleEntity animatable, float partialTick, PoseStack poseStack,
									MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight,
									ResourceLocation texture) {
		return RenderType.entityCutout(getTextureLocation(animatable));
	}

	@Override
	public void render(GeoModel model, GeoExampleEntity animatable, float partialTick, RenderType type,
					   PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer buffer,
					   int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
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
		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
