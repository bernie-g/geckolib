// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLibMod
// Paste this class into your mod and follow the documentation for GeckoLibMod to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.model.tile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FertilizerModel extends AnimatedGeoModel<FertilizerTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(FertilizerTileEntity animatable) {
		if (animatable.getLevel().isRaining()) {
			return new ResourceLocation(GeckoLib.ModID, "animations/fertilizer.animation.json");
		} else {
			return new ResourceLocation(GeckoLib.ModID, "animations/botarium.animation.json");
		}
	}

	@Override
	public ResourceLocation getModelResource(FertilizerTileEntity animatable) {
		if (animatable.getLevel().isRaining()) {
			return new ResourceLocation(GeckoLib.ModID, "geo/fertilizer.geo.json");
		} else {
			return new ResourceLocation(GeckoLib.ModID, "geo/botarium.geo.json");
		}
	}

	@Override
	public ResourceLocation getTextureResource(FertilizerTileEntity entity) {
		if (entity.getLevel().isRaining()) {
			return new ResourceLocation(GeckoLib.ModID + ":textures/block/fertilizer.png");
		} else {
			return new ResourceLocation(GeckoLib.ModID + ":textures/block/botarium.png");
		}
	}
}