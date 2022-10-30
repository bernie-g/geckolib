package software.bernie.example.client.renderer.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.example.client.model.entity.RocketModel;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class RocketRender extends GeoProjectilesRenderer<RocketProjectile> {

	public RocketRender(EntityRendererFactory.Context renderManagerIn) {
		super(renderManagerIn, new RocketModel());
	}

	protected int getBlockLight(RocketProjectile entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public RenderLayer getRenderType(RocketProjectile animatable, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight,
			Identifier texture) {
		return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
	}
}
