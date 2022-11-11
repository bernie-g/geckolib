package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import software.bernie.example.client.model.entity.BatModel;
import software.bernie.example.entity.BatEntity;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib3.renderer.GeoRenderer} for {@link BatEntity}
 * @see BatModel
 */
public class BatRenderer extends GeoEntityRenderer<BatEntity> {
	private int currentTick = -1;

	public BatRenderer(EntityRendererProvider.Context context) {
		super(context, new BatModel());
	}

	// Add some particles around the ear when rendering
	@Override
	public void postRender(BatEntity animatable, PoseStack poseStack, float partialTick,
						   MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight,
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

		super.postRender(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlay, red, green, blue, alpha);
	}
}
