package software.bernie.example.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.example.client.model.tile.HabitatModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class HabitatTileRenderer extends GeoBlockRenderer<HabitatTileEntity> {
	public HabitatTileRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		super(rendererDispatcherIn, new HabitatModel());
	}

	@Override
	public RenderType getRenderType(HabitatTileEntity animatable, float partialTick, PoseStack poseStack,
									MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight,
									ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
