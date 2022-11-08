package software.bernie.example.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.animatable.GeoEntity;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.AnimationData;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example {@link GeoAnimatable} implementation of an entity
 */
public class ParasiteEntity extends Monster implements GeoEntity {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public ParasiteEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	// Add our attack animation
	@Override
	public void registerControllers(AnimationData<?> data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 5, event -> {
			event.getController().setAnimation(DefaultAnimations.ATTACK_STRIKE);

			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}