// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLibMod
// Paste this class into your mod and follow the documentation for GeckoLibMod to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.model.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.entity.FertilizerBlockEntity;
import software.bernie.example.client.renderer.block.FertilizerBlockRenderer;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedBlockGeoModel;
import software.bernie.geckolib3.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link FertilizerBlockEntity}
 * @see FertilizerBlockEntity
 * @see FertilizerBlockRenderer
 */
public class FertilizerModel extends DefaultedBlockGeoModel<FertilizerBlockEntity> {
	private final ResourceLocation BOTARIUM_MODEL = buildFormattedModelPath(new ResourceLocation(GeckoLib.MOD_ID, "botarium"));
	private final ResourceLocation BOTARIUM_TEXTURE = buildFormattedTexturePath(new ResourceLocation(GeckoLib.MOD_ID, "botarium"));
	private final ResourceLocation BOTARIUM_ANIMATIONS = buildFormattedAnimationPath(new ResourceLocation(GeckoLib.MOD_ID, "botarium"));

	public FertilizerModel() {
		super(new ResourceLocation(GeckoLib.MOD_ID, "fertilizer"));
	}

	/**
	 * Return the fertilizer animation path if it's raining, or the botarium animation path if not.
	 */
	@Override
	public ResourceLocation getAnimationResource(FertilizerBlockEntity animatable) {
		if (animatable.getLevel().isRaining()) {
			return super.getAnimationResource(animatable);
		}
		else {
			return BOTARIUM_ANIMATIONS;
		}
	}

	/**
	 * Return the fertilizer model path if it's raining, or the botarium model path if not.
	 */
	@Override
	public ResourceLocation getModelResource(FertilizerBlockEntity animatable) {
		if (animatable.getLevel().isRaining()) {
			return super.getModelResource(animatable);
		}
		else {
			return BOTARIUM_MODEL;
		}
	}

	/**
	 * Return the fertilizer texture path if it's raining, or the botarium texture path if not.
	 */
	@Override
	public ResourceLocation getTextureResource(FertilizerBlockEntity animatable) {
		if (animatable.getLevel().isRaining()) {
			return super.getTextureResource(animatable);
		}
		else {
			return BOTARIUM_TEXTURE;
		}
	}

	@Override
	public RenderType getRenderType(FertilizerBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureResource(animatable));
	}
}