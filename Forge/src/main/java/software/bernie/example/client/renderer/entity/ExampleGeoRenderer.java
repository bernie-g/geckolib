package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import software.bernie.example.client.model.entity.ExampleEntityModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {
	public ExampleGeoRenderer(EntityRendererManager renderManager) {
		super(renderManager, new ExampleEntityModel());
	}

	@Override
	public RenderType getRenderType(GeoExampleEntity animatable, float partialTicks, MatrixStack stack,
			IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
	
	private int currentTick = -1;

	@Override
	public void render(GeoModel model, GeoExampleEntity animatable, float partialTicks, RenderType type,
			MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder,
			int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if(currentTick < 0 || currentTick != animatable.tickCount) {
			this.currentTick = animatable.tickCount;
			
			if (model.getBone("leftear").isPresent()) {
				animatable.getCommandSenderWorld().addParticle(ParticleTypes.PORTAL,
						model.getBone("leftear").get().getWorldPosition().x,
						model.getBone("leftear").get().getWorldPosition().y,
						model.getBone("leftear").get().getWorldPosition().z, (animatable.getRandom().nextDouble() - 0.5D),
						-animatable.getRandom().nextDouble(), (animatable.getRandom().nextDouble() - 0.5D));
			}
		}
		
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
