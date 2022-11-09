package software.bernie.example.entity;

import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib3.animatable.GeoEntity;
import software.bernie.geckolib3.animatable.GeoReplacedEntity;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animation.AnimationData;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Replacement {@link net.minecraft.world.entity.monster.Creeper} {@link GeoEntity} to showcase
 * replacing the model and animations of an existing entity
 * @see software.bernie.geckolib3.renderer.GeoReplacedEntityRenderer
 * @see software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer
 * @see software.bernie.example.client.model.entity.ReplacedCreeperModel
 */
public class ReplacedCreeperEntity implements GeoReplacedEntity {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	/**
	 * Register the idle + walk animations for the entity.<br>
	 * In this situation we're going to use a generic controller that is already built for us
	 * @see software.bernie.geckolib3.constant.DefaultAnimations
	 */
	@Override
	public void registerControllers(AnimationData<?> data) {
		data.addAnimationController(DefaultAnimations.genericWalkIdleController(this));
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}

	@Override
	public EntityType<?> getReplacingEntityType() {
		return EntityType.CREEPER;
	}
}
