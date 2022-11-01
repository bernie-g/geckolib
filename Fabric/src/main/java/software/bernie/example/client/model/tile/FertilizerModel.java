package software.bernie.example.client.model.tile;

import net.minecraft.util.Identifier;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FertilizerModel extends AnimatedGeoModel<FertilizerTileEntity> {
	@Override
	public Identifier getAnimationFileLocation(FertilizerTileEntity animatable) {
		if (animatable.getWorld().isRaining())
			return EntityResources.FERTILIZER_ANIMATIONS;

		return EntityResources.BOTARIUM_ANIMATIONS;
	}

	@Override
	public Identifier getModelLocation(FertilizerTileEntity animatable) {
		if (animatable.getWorld().isRaining())
			return EntityResources.FERTILIZER_MODEL;

		return EntityResources.BOTARIUM_MODEL;
	}

	@Override
	public Identifier getTextureLocation(FertilizerTileEntity entity) {
		if (entity.getWorld().isRaining())
			return EntityResources.FERTILIZER_TEXTURE;

		return EntityResources.BOTARIUM_TEXTURE;
	}
}