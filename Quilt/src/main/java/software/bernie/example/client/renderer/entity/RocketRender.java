package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.model.entity.RocketModel;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.geckolib3q.renderers.geo.GeoProjectilesRenderer;

public class RocketRender extends GeoProjectilesRenderer<RocketProjectile> {

	public RocketRender(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, new RocketModel());
	}

	protected int getBlockLight(RocketProjectile entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public RenderType getRenderType(RocketProjectile animatable, float partialTicks, PoseStack stack,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureResource(animatable));
	}
}
