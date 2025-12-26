package software.bernie.geckolib.model;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;

/**
 * {@link DefaultedGeoModel} specific to {@link net.minecraft.world.entity.Entity Entities}
 * <p>
 * Using this class pre-sorts provided asset paths into the "entity" subdirectory
 * <p>
 * Additionally it can automatically handle head-turning if the entity has a "head" bone
 */
public class DefaultedEntityGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
    protected final @Nullable String headBone;

	/**
	 * Create a new instance of this model class
	 * <p>
	 * The asset path should be the truncated relative path from the base folder
	 * <p>
	 * E.G.
	 * <pre>{@code
	 * 	new Identifier("myMod", "animals/red_fish")
	 * }</pre>
	 */
	public DefaultedEntityGeoModel(Identifier assetSubpath) {
		this(assetSubpath, null);
	}

    /**
     * Create a new instance of this model class, preconfigured to automatically handle head-turning for bone matching the provided name
     * <p>
     * The asset path should be the truncated relative path from the base folder
     * <p>
     * E.G.
     * <pre>{@code
     * 	new Identifier("myMod", "animals/red_fish")
     * }</pre>
     */
	public DefaultedEntityGeoModel(Identifier assetSubpath, @Nullable String headBone) {
		super(assetSubpath);

        this.headBone = headBone;
	}

	/**
	 * Returns the subtype string for this type of model
	 * <p>
	 * This allows for sorting of asset files into neat subdirectories for clean management.
	 */
	@Override
	protected String subtype() {
		return "entity";
	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltModel(Identifier altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltModel(altPath);
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltAnimations(Identifier altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltAnimations(altPath);
	}

	/**
	 * Changes the constructor-defined texture path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltTexture(Identifier altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltTexture(altPath);
	}
}
