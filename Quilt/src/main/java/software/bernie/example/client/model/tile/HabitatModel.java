package software.bernie.example.client.model.tile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

/**
 * @author VoutVouniern Copyright (c) 03.06.2022 Developed by VoutVouniern
 */
public class HabitatModel extends AnimatedGeoModel<HabitatTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(HabitatTileEntity entity) {
		return EntityResources.HABITAT_ANIMATIONS;
	}

	@Override
	public ResourceLocation getModelResource(HabitatTileEntity animatable) {
		return EntityResources.HABITAT_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(HabitatTileEntity entity) {
		return EntityResources.HABITAT_TEXTURE;
	}
}