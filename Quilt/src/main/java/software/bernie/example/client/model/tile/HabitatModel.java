package software.bernie.example.client.model.tile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @author VoutVouniern Copyright (c) 03.06.2022 Developed by VoutVouniern
 */
public class HabitatModel extends AnimatedGeoModel<HabitatTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(HabitatTileEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/gecko_habitat.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HabitatTileEntity animatable) {
		return new ResourceLocation(GeckoLib.ModID, "geo/gecko_habitat.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HabitatTileEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/block/gecko_habitat.png");
	}
}