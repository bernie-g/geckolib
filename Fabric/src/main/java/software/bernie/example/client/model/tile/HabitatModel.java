package software.bernie.example.client.model.tile;

import net.minecraft.util.Identifier;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @author VoutVouniern Copyright (c) 03.06.2022 Developed by VoutVouniern
 */
public class HabitatModel extends AnimatedGeoModel<HabitatTileEntity> {
	@Override
	public Identifier getAnimationFileLocation(HabitatTileEntity entity) {
		return new Identifier(GeckoLib.ModID, "animations/gecko_habitat.animation.json");
	}

	@Override
	public Identifier getModelLocation(HabitatTileEntity animatable) {
		return new Identifier(GeckoLib.ModID, "geo/gecko_habitat.geo.json");
	}

	@Override
	public Identifier getTextureLocation(HabitatTileEntity entity) {
		return new Identifier(GeckoLib.ModID, "textures/block/gecko_habitat.png");
	}
}