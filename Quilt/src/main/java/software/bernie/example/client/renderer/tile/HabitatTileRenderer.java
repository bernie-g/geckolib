package software.bernie.example.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
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
	public RenderType getRenderType(HabitatTileEntity animatable, float partialTicks, PoseStack stack,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
