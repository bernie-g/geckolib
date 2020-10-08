// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.renderer.model.tile;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class BotariumModel extends AnimatedGeoModel<BotariumTileEntity>
{
	@Override
	public ResourceLocation getAnimationFileLocation(BotariumTileEntity entity)
	{
		return new ResourceLocation(GeckoLib.ModID, "animations/jackinthebox.animation.json");

	}

	@Override
	public ResourceLocation getModelLocation(BotariumTileEntity animatable)
	{
		return new ResourceLocation(GeckoLib.ModID, "geo/jack_in_the_box_model.json");
	}

	@Override
	public ResourceLocation getTextureLocation(BotariumTileEntity entity)
	{
		return new ResourceLocation(GeckoLib.ModID, "textures/item/jack.png");
	}
}