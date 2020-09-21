// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.renderer.model.tile;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class FertilizerModel extends AnimatedGeoModel<FertilizerTileEntity>
{
	@Override
	public ResourceLocation getAnimationFileLocation(FertilizerTileEntity entity)
	{
		return new ResourceLocation("geckolib", "animations/fertilizer_anim.json");
	}

	@Override
	public ResourceLocation getModelLocation(FertilizerTileEntity animatable)
	{
		if(animatable.getWorld().isRaining())
		{
			return new ResourceLocation("geckolib", "geo/fertilizer.json");
		}
		else {
			return new ResourceLocation("geckolib", "geo/botarium_block.json");
		}
	}

	@Override
	public ResourceLocation getTextureLocation(FertilizerTileEntity entity)
	{
		if(entity.getWorld().isRaining())
		{
			return new ResourceLocation("geckolib" + ":textures/block/fertilizer.png");
		}
		else {
			return new ResourceLocation("geckolib" + ":textures/block/cloche.png");
		}
	}
}