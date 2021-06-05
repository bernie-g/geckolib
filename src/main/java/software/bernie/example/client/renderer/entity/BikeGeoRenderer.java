package software.bernie.example.client.renderer.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.example.client.model.entity.BikeModel;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BikeGeoRenderer extends GeoEntityRenderer<BikeEntity> {

	public BikeGeoRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new BikeModel());
	}

	@Override
	public RenderLayer getRenderType(BikeEntity animatable, float partialTicks, MatrixStack stack,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
	}
}