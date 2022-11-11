package software.bernie.geckolib3.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.constant.DataTickets;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animatable.model.GeoBone;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.model.data.EntityModelData;

/**
 * {@link DefaultedGeoModel} specific to {@link net.minecraft.world.entity.Entity Entities}.
 * Using this class pre-sorts provided asset paths into the "entity" subdirectory
 * Additionally it can automatically handle head-turning if the entity has a "head" bone
 */
public class DefaultedEntityGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
	private final boolean turnsHead;

	/**
	 * Create a new instance of this model class.<br>
	 * The asset path should be the truncated relative path from the base folder.<br>
	 * E.G.
	 * <pre>{@code
	 * 	new ResourceLocation("myMod", "animals/red_fish")
	 * }</pre>
	 */
	public DefaultedEntityGeoModel(ResourceLocation assetSubpath) {
		this(assetSubpath, false);
	}

	public DefaultedEntityGeoModel(ResourceLocation assetSubpath, boolean turnsHead) {
		super(assetSubpath);

		this.turnsHead = turnsHead;
	}

	@Override
	protected String subtype() {
		return "entity";
	}

	@Override
	public void setCustomAnimations(T animatable, int instanceId, AnimationEvent<T> animationEvent) {
		if (!this.turnsHead)
			return;

		GeoBone head = getAnimationProcessor().getBone("head");

		if (head != null) {
			EntityModelData entityData = animationEvent.getData(DataTickets.ENTITY_MODEL_DATA);

			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}

	/**
	 * Changes the constructor-defined model path for this model to an alternate.<br>
	 * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and animations for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltModel(ResourceLocation altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltModel(altPath);
	}

	/**
	 * Changes the constructor-defined animations path for this model to an alternate.<br>
	 * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and texture for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltAnimations(ResourceLocation altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltAnimations(altPath);
	}

	/**
	 * Changes the constructor-defined texture path for this model to an alternate.<br>
	 * This is useful if your animatable shares a texture path with another animatable that differs in path to the model and animations for this model
	 */
	@Override
	public DefaultedEntityGeoModel<T> withAltTexture(ResourceLocation altPath) {
		return (DefaultedEntityGeoModel<T>)super.withAltTexture(altPath);
	}
}
