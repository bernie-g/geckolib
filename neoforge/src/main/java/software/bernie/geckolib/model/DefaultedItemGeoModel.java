package software.bernie.geckolib.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

/**
 * {@link DefaultedGeoModel} specific to {@link net.minecraft.world.item.Item Items}.
 * Using this class pre-sorts provided asset paths into the "item" subdirectory
 */
public class DefaultedItemGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
	/**
	 * Create a new instance of this model class.<br>
	 * The asset path should be the truncated relative path from the base folder.<br>
	 * E.G.
	 * <pre>{@code
	 * 	new ResourceLocation("myMod", "armor/obsidian")
	 * }</pre>
	 */
	public DefaultedItemGeoModel(ResourceLocation assetSubpath) {
		super(assetSubpath);
	}

	@Override
	protected String subtype() {
		return "item";
	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate.<br>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	@Override
	public DefaultedItemGeoModel<T> withAltModel(ResourceLocation altPath) {
		return (DefaultedItemGeoModel<T>)super.withAltModel(altPath);
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate.<br>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	@Override
	public DefaultedItemGeoModel<T> withAltAnimations(ResourceLocation altPath) {
		return (DefaultedItemGeoModel<T>)super.withAltAnimations(altPath);
	}

	/**
	 * Changes the constructor-defined texture path for this model to an alternate.<br>
	 * This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	 */
	@Override
	public DefaultedItemGeoModel<T> withAltTexture(ResourceLocation altPath) {
		return (DefaultedItemGeoModel<T>)super.withAltTexture(altPath);
	}
}
