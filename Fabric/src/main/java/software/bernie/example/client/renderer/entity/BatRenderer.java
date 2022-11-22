package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import software.bernie.example.client.model.entity.BatModel;
import software.bernie.example.entity.BatEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/**
 * Example {@link software.bernie.geckolib.renderer.GeoRenderer} for {@link BatEntity}
 * @see BatModel
 */
public class BatRenderer extends GeoEntityRenderer<BatEntity> {
	private int currentTick = -1;

	public BatRenderer(EntityRendererProvider.Context context) {
		super(context, new BatModel());

		// Add the glow layer to the bat so that it can live out its dreams of being rudolph
		addRenderLayer(new AutoGlowingGeoLayer<>(this));
	}

	// Add some particles around the ear when rendering
	@Override
	public void postRender(PoseStack poseStack, BatEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight,
						   int packedOverlay, float red, float green, float blue, float alpha) {
		if (this.currentTick < 0 || this.currentTick != animatable.tickCount) {
			this.currentTick = animatable.tickCount;

			// Find the earbone and use it as the point of reference
			this.model.getBone("leftear").ifPresent(ear -> {
				RandomSource rand = animatable.getRandom();

				animatable.getCommandSenderWorld().addParticle(ParticleTypes.PORTAL,
						ear.getWorldPosition().x,
						ear.getWorldPosition().y,
						ear.getWorldPosition().z,
						rand.nextDouble() - 0.5D,
						-rand.nextDouble(),
						rand.nextDouble() - 0.5D);
			});
		}

		super.postRender(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight,
				packedOverlay, red, green, blue, alpha);
	}
}
