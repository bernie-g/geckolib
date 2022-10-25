package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.example.client.model.entity.CarModel;
import software.bernie.example.entity.CarEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CarGeoRenderer extends GeoEntityRenderer<CarEntity> {

	public CarGeoRenderer(EntityRendererManager ctx) {
		super(ctx, new CarModel());
	}

	@Override
	public RenderType getRenderType(CarEntity animatable, float partialTicks, MatrixStack stack,
			IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}