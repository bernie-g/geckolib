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
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {
	public ExampleGeoRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new ExampleEntityModel());
	}

	@Override
	public RenderType getRenderType(GeoExampleEntity animatable, float partialTicks, PoseStack stack,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityCutout(getTextureLocation(animatable));
	}
	
	@Override
	public void render(GeoModel model, GeoExampleEntity animatable, float partialTicks, RenderType type,
			PoseStack matrixStackIn, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder,
			int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (model.getBone("leftear").isPresent()) {
			animatable.getCommandSenderWorld().addParticle(ParticleTypes.PORTAL,
					model.getBone("leftear").get().getWorldPosition().x,
					model.getBone("leftear").get().getWorldPosition().y,
					model.getBone("leftear").get().getWorldPosition().z, (animatable.getRandom().nextDouble() - 0.5D),
					-animatable.getRandom().nextDouble(), (animatable.getRandom().nextDouble() - 0.5D));
		}
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, alpha);
	}

}
