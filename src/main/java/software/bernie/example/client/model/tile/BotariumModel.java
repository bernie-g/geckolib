// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLibMod
// Paste this class into your mod and follow the documentation for GeckoLibMod to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.model.tile;

import net.minecraft.util.Identifier;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BotariumModel extends AnimatedGeoModel<BotariumTileEntity> {
	@Override
	public Identifier getAnimationResource(BotariumTileEntity entity) {
		return new Identifier(GeckoLib.ModID, "animations/botarium.animation.json");
	}

	@Override
	public Identifier getModelResource(BotariumTileEntity animatable) {
		return new Identifier(GeckoLib.ModID, "geo/botarium.geo.json");
	}

	@Override
	public Identifier getTextureResource(BotariumTileEntity entity) {
		return new Identifier(GeckoLib.ModID, "textures/block/botarium.png");
	}
}