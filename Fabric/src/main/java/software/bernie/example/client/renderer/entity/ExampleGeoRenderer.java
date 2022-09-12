package software.bernie.example.client.renderer.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.example.client.model.entity.ExampleEntityModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.LayerGlowingAreasGeo;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {
	public ExampleGeoRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new ExampleEntityModel());
		this.addLayer(new LayerGlowingAreasGeo<GeoExampleEntity>(this, modelProvider::getTextureResource,
				modelProvider::getModelResource, RenderLayer::getEyes));
	}

	@Override
	public RenderLayer getRenderType(GeoExampleEntity animatable, float partialTicks, MatrixStack stack,
			@Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder,
			int packedLightIn, Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(this.getTextureResource(animatable));
	}

}
