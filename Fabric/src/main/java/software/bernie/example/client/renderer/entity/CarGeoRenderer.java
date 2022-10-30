package software.bernie.example.client.renderer.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.example.client.model.entity.CarModel;
import software.bernie.example.entity.CarEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CarGeoRenderer extends GeoEntityRenderer<CarEntity> {

	public CarGeoRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new CarModel());
	}

	@Override
	public RenderLayer getRenderType(CarEntity animatable, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight,
			Identifier texture) {
		return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
	}
}