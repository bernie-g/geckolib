package software.bernie.example.client.model.tile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class FertilizerModel extends AnimatedGeoModel<FertilizerTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(FertilizerTileEntity animatable) {
		if (animatable.getLevel().isRaining())
			return EntityResources.FERTILIZER_ANIMATIONS;

		return EntityResources.BOTARIUM_ANIMATIONS;
	}

	@Override
	public ResourceLocation getModelResource(FertilizerTileEntity animatable) {
		if (animatable.getLevel().isRaining())
			return EntityResources.FERTILIZER_MODEL;

		return EntityResources.BOTARIUM_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(FertilizerTileEntity entity) {
		if (entity.getLevel().isRaining())
			return EntityResources.FERTILIZER_TEXTURE;

		return EntityResources.BOTARIUM_TEXTURE;
	}
}