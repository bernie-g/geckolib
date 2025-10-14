package software.bernie.geckolib.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;

/**
 * {@link DefaultedGeoModel} specific to {@link net.minecraft.world.entity.Entity Entities}
 * <p>
 * Using this class pre-sorts provided asset paths into the "entity" subdirectory
 * <p>
 * Additionally it can automatically handle head-turning if the entity has a "head" bone
 */
public class DefaultedEntityGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
    @Nullable
    protected String headBone;

	/**
	 * Create a new instance of this model class
	 * <p>
	 * The asset path should be the truncated relative path from the base folder
	 * <p>
	 * E.G.
	 * <pre>{@code
	 * 	new ResourceLocation("myMod", "animals/red_fish")
	 * }</pre>
	 */
	public DefaultedEntityGeoModel(ResourceLocation assetSubpath) {
		this(assetSubpath, null);
	}

    /**
     * Create a new instance of this model class, preconfigured to automatically handle head-turning for bone matching the provided name
     * <p>
     * The asset path should be the truncated relative path from the base folder
     * <p>
     * E.G.
     * <pre>{@code
     * 	new ResourceLocation("myMod", "animals/red_fish")
     * }</pre>
     */
	public DefaultedEntityGeoModel(ResourceLocation assetSubpath, @Nullable String headBone) {
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

	@Override
	public void setCustomAnimations(AnimationState<T> animationState) {
		if (this.headBone == null)
			return;

		GeoBone head = getAnimationProcessor().getBone(this.headBone);

		if (head != null) {
			float pitch = animationState.getData(DataTickets.ENTITY_PITCH);
			float yaw = animationState.getData(DataTickets.ENTITY_YAW);

			head.setRotX(-pitch * Mth.DEG_TO_RAD);
			head.setRotY(-yaw * Mth.DEG_TO_RAD);
		}
	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltModel(ResourceLocation altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltModel(altPath);
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltAnimations(ResourceLocation altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltAnimations(altPath);
	}

	/**
	 * Changes the constructor-defined texture path for this model to an alternate
	 * <p>
	 * This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltTexture(ResourceLocation altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltTexture(altPath);
	}
}
