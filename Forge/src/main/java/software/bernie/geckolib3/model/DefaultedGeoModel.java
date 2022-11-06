package software.bernie.geckolib3.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;

/**
 * Defaulted model class for GeckoLib models.<br>
 * This class allows for minimal boilerplate when implementing basic models, and saves on new classes.<br>
 * Additionally, it encourages consistency and sorting of asset paths.
 */
public abstract class DefaultedGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
	private ResourceLocation modelPath;
	private ResourceLocation texturePath;
	private ResourceLocation animationsPath;

	/**
	 * Create a new instance of this model class.<br>
	 * The asset path should be the truncated relative path from the base folder.<br>
	 * E.G.
	 * <pre>
	 *     {@code
	 *		new ResourceLocation("myMod", "animals/red_fish")
	 *		}</pre>
	 * @param assetSubpath
	 */
	public DefaultedGeoModel(ResourceLocation assetSubpath) {
		this.modelPath = new ResourceLocation(assetSubpath.getNamespace(), "geo/" + subtype() + "/" + assetSubpath.getPath() + ".geo.json");
		this.texturePath = new ResourceLocation(assetSubpath.getNamespace(), "textures/" + subtype() + "/" + assetSubpath.getPath() + ".png");
		this.animationsPath = new ResourceLocation(assetSubpath.getNamespace(), "animations/" + subtype() + "/" + assetSubpath.getPath() + ".animation.json");
	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate.<br>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	public DefaultedGeoModel<T> withAltModel(ResourceLocation altPath) {
		this.modelPath = new ResourceLocation(altPath.getNamespace(), "geo/" + subtype() + "/" + altPath + ".geo.json");

		return this;
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate.<br>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	public DefaultedGeoModel<T> withAltAnimations(ResourceLocation altPath) {
		this.animationsPath = new ResourceLocation(altPath.getNamespace(), "animations/" + subtype() + "/" + altPath + ".animations.json");

		return this;
	}

	/**
	 * Changes the constructor-defined texture path for this model to an alternate.<br>
	 * This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	 */
	public DefaultedGeoModel<T> withAltTexture(ResourceLocation altPath) {
		this.texturePath = new ResourceLocation(altPath.getNamespace(), "textures/" + subtype() + "/" + altPath + ".png");

		return this;
	}

	/**
	 * Returns the subtype string for this type of model.<br>
	 * This allows for sorting of asset files into neat subdirectories for clean management.
	 * Examples:
	 * <ul>
	 *     <li>"entity"</li>
	 *     <li>"block"</li>
	 *     <li>"item"</li>
	 * </ul>
	 */
	protected abstract String subtype();

	@Override
	public ResourceLocation getModelResource(T animatable) {
		return this.modelPath;
	}

	@Override
	public ResourceLocation getTextureResource(T animatable) {
		return this.texturePath;
	}

	@Override
	public ResourceLocation getAnimationResource(T animatable) {
		return this.animationsPath;
	}
}
