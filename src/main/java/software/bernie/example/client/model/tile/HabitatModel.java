// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLibMod
// Paste this class into your mod and follow the documentation for GeckoLibMod to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.model.tile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HabitatModel extends AnimatedGeoModel<HabitatTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(HabitatTileEntity animatable) {
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