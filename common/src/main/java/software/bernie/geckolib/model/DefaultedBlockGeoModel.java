package software.bernie.geckolib.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

/**
 * {@link DefaultedGeoModel} specific to {@link net.minecraft.world.level.block.Block Blocks}.
 * Using this class pre-sorts provided asset paths into the "block" subdirectory
 */
public class DefaultedBlockGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
	/**
	 * Create a new instance of this model class.<br>
	 * The asset path should be the truncated relative path from the base folder.<br>
	 * E.G.
	 * <pre>{@code
	 * 	new ResourceLocation("myMod", "workbench/sawmill")
	 * }</pre>
	 */
	public DefaultedBlockGeoModel(ResourceLocation assetSubpath) {
		super(assetSubpath);
	}

	@Override
	protected String subtype() {
		return "block";
	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate.<br>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	@Override
	public DefaultedBlockGeoModel<T> withAltModel(ResourceLocation altPath) {
		return (DefaultedBlockGeoModel<T>)super.withAltModel(altPath);
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate.<br>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	@Override
	public DefaultedBlockGeoModel<T> withAltAnimations(ResourceLocation altPath) {
		return (DefaultedBlockGeoModel<T>)super.withAltAnimations(altPath);
	}

	/**
	 * Changes the constructor-defined texture path for this model to an alternate.<br>
	 * This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	 */
	@Override
	public DefaultedBlockGeoModel<T> withAltTexture(ResourceLocation altPath) {
		return (DefaultedBlockGeoModel<T>)super.withAltTexture(altPath);
	}
}
