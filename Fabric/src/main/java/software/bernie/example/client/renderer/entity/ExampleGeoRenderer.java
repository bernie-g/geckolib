package software.bernie.example.client.renderer.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import software.bernie.example.client.model.entity.ExampleEntityModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {

	private int currentTick = -1;

	public ExampleGeoRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new ExampleEntityModel());
	}

	@Override
	public RenderLayer getRenderType(GeoExampleEntity animatable, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight,
			Identifier texture) {
		return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
	}

	@Override
	public void render(GeoModel model, GeoExampleEntity animatable, float partialTick, RenderLayer type,
			MatrixStack poseStack, VertexConsumerProvider bufferSource, VertexConsumer buffer,
			int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (currentTick < 0 || currentTick != animatable.age) {
			this.currentTick = animatable.age;
			if (model.getBone("leftear").isPresent()) {
				animatable.world.addParticle(ParticleTypes.PORTAL, model.getBone("leftear").get().getWorldPosition().x,
						model.getBone("leftear").get().getWorldPosition().y,
						model.getBone("leftear").get().getWorldPosition().z,
						(animatable.getRandom().nextDouble() - 0.5D), -animatable.getRandom().nextDouble(),
						(animatable.getRandom().nextDouble() - 0.5D));
			}
		}
		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer,
				packedLight, packedOverlay, red, green, blue, alpha);
	}

}
