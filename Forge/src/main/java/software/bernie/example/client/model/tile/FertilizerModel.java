package software.bernie.example.client.model.tile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FertilizerModel extends AnimatedGeoModel<FertilizerTileEntity> {
	@Override
	public ResourceLocation getAnimationFileLocation(FertilizerTileEntity animatable) {
		if (animatable.getLevel().isRaining())
			return EntityResources.FERTILIZER_ANIMATIONS;

		return EntityResources.BOTARIUM_ANIMATIONS;
	}

	@Override
	public ResourceLocation getModelLocation(FertilizerTileEntity animatable) {
		if (animatable.getLevel().isRaining())
			return EntityResources.FERTILIZER_MODEL;

		return EntityResources.BOTARIUM_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(FertilizerTileEntity entity) {
		if (entity.getLevel().isRaining())
			return EntityResources.FERTILIZER_TEXTURE;

		return EntityResources.BOTARIUM_TEXTURE;
	}
}