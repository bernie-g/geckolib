package software.bernie.example.client.renderer.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.example.client.model.entity.BikeModel;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class BikeGeoRenderer extends GeoEntityRenderer<BikeEntity>
{
	public BikeGeoRenderer(EntityRenderDispatcher renderDispatcher)
	{
		super(renderDispatcher, new BikeModel());
	}

	@Override
	public RenderLayer getRenderType(BikeEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
	}
}