package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.model.entity.TestModel;
import software.bernie.example.entity.TestEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class TestGeoRenderer extends GeoEntityRenderer<TestEntity> {
	public TestGeoRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new TestModel());
	}

	@Override
	public RenderType getRenderType(TestEntity animatable, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
