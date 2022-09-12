package software.bernie.example.client.renderer.tile;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.client.model.tile.BotariumModel;
import software.bernie.geckolib3q.renderers.geo.GeoBlockRenderer;

public class BotariumTileRenderer extends GeoBlockRenderer<BotariumTileEntity> {
	public BotariumTileRenderer() {
		super(new BotariumModel());
	}

	@Override
	public RenderLayer getRenderType(BotariumTileEntity animatable, float partialTicks, MatrixStack stack,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
	}
}
