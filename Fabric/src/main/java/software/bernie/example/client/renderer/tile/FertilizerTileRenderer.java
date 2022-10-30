package software.bernie.example.client.renderer.tile;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.model.tile.FertilizerModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class FertilizerTileRenderer extends GeoBlockRenderer<FertilizerTileEntity> {
	public FertilizerTileRenderer() {
		super(new FertilizerModel());
	}

	@Override
	public RenderLayer getRenderType(FertilizerTileEntity animatable, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight,
			Identifier texture) {
		return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
	}
}
