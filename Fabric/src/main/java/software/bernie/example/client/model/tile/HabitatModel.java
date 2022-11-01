package software.bernie.example.client.model.tile;

import net.minecraft.util.Identifier;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @author VoutVouniern Copyright (c) 03.06.2022 Developed by VoutVouniern
 */
public class HabitatModel extends AnimatedGeoModel<HabitatTileEntity> {
	@Override
	public Identifier getAnimationFileLocation(HabitatTileEntity entity) {
		return EntityResources.HABITAT_ANIMATIONS;
	}

	@Override
	public Identifier getModelLocation(HabitatTileEntity animatable) {
		return EntityResources.HABITAT_MODEL;
	}

	@Override
	public Identifier getTextureLocation(HabitatTileEntity entity) {
		return EntityResources.HABITAT_TEXTURE;
	}
}