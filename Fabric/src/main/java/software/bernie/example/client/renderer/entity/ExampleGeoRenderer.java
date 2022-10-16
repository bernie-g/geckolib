package software.bernie.example.client.renderer.entity;

import org.jetbrains.annotations.Nullable;

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
	public RenderLayer getRenderType(GeoExampleEntity animatable, float partialTicks, MatrixStack stack,
			@Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder,
			int packedLightIn, Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
	}

	@Override
	public void render(GeoModel model, GeoExampleEntity animatable, float partialTicks, RenderLayer type,
			MatrixStack matrixStackIn, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
			int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
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
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

}
