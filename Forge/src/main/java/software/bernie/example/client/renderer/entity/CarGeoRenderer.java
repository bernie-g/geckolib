package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.model.entity.CarModel;
import software.bernie.example.entity.CarEntity;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

public class CarGeoRenderer extends GeoEntityRenderer<CarEntity> {

	public CarGeoRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new CarModel());
	}

	@Override
	public RenderType getRenderType(PoseStack poseStack, CarEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}