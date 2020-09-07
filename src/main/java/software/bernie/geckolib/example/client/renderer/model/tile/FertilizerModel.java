// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.geckolib.example.client.renderer.model.tile;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.block.tile.FertilizerTileEntity;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class FertilizerModel extends AnimatedGeoModel<FertilizerTileEntity>
{
	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return new ResourceLocation("geckolib", "animations/fertilizer_anim.json");
	}

	@Override
	public ResourceLocation getModelLocation()
	{
		return new ResourceLocation("geckolib", "geo/fertilizer.json");
	}
}