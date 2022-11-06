package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.model.entity.BikeModel;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

public class BikeGeoRenderer extends GeoEntityRenderer<BikeEntity> {
	public BikeGeoRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new BikeModel());
	}

	@Override
	public RenderType getRenderType(PoseStack poseStack, BikeEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
