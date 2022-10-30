package software.bernie.example.client.renderer.tile;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.example.client.model.tile.HabitatModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

/**
 * @author VoutVouniern Copyright (c) 03.06.2022 Developed by VoutVouniern
 */
public class HabitatTileRenderer extends GeoBlockRenderer<HabitatTileEntity> {
	public HabitatTileRenderer() {
		super(new HabitatModel());
	}

	@Override
	public RenderLayer getRenderType(HabitatTileEntity animatable, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight,
									Identifier texture) {
		return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
	}
}
