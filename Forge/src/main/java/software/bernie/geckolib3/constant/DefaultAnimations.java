package software.bernie.geckolib3.constant;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.core.animation.RawAnimation;
import software.bernie.geckolib3.core.object.PlayState;

import java.util.function.BiFunction;

/**
 * Optionally usable class that holds constants for recommended animation paths.<br>
 * Using these won't affect much, but it may help keep some consistency in animation namings.<br>
 * Additionally, it encourages use of cached {@link software.bernie.geckolib3.core.animation.RawAnimation RawAnimations}, to reduce overheads.
 */
public final class DefaultAnimations {
	public static final RawAnimation ITEM_ON_USE = RawAnimation.begin().thenPlay("item.use");

	public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
	public static final RawAnimation LIVING = RawAnimation.begin().thenLoop("misc.living");
	public static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("misc.spawn");
	public static final RawAnimation INTERACT = RawAnimation.begin().thenPlay("misc.interact");
	public static final RawAnimation DEPLOY = RawAnimation.begin().thenPlay("misc.deploy");
	public static final RawAnimation REST = RawAnimation.begin().thenPlay("misc.rest");
	public static final RawAnimation SIT = RawAnimation.begin().thenPlayAndHold("misc.sit");

	public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
	public static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
	public static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
	public static final RawAnimation DRIVE = RawAnimation.begin().thenLoop("move.drive");
	public static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
	public static final RawAnimation CRAWL = RawAnimation.begin().thenLoop("move.crawl");
	public static final RawAnimation JUMP = RawAnimation.begin().thenPlay("move.jump");
	public static final RawAnimation SNEAK = RawAnimation.begin().thenLoop("move.sneak");

	public static final RawAnimation ATTACK_SWING = RawAnimation.begin().thenPlay("attack.swing");
	public static final RawAnimation ATTACK_THROW = RawAnimation.begin().thenPlay("attack.throw");
	public static final RawAnimation ATTACK_BITE = RawAnimation.begin().thenPlay("attack.bite");
	public static final RawAnimation ATTACK_SLAM = RawAnimation.begin().thenPlay("attack.slam");
	public static final RawAnimation ATTACK_STOMP = RawAnimation.begin().thenPlay("attack.stomp");
	public static final RawAnimation ATTACK_STRIKE = RawAnimation.begin().thenPlay("attack.strike");
	public static final RawAnimation ATTACK_FLYING_ATTACK = RawAnimation.begin().thenPlay("attack.flying_attack");
	public static final RawAnimation ATTACK_SHOOT = RawAnimation.begin().thenPlay("attack.shoot");
	public static final RawAnimation ATTACK_BLOCK = RawAnimation.begin().thenPlay("attack.block");
	public static final RawAnimation ATTACK_CHARGE = RawAnimation.begin().thenPlay("attack.charge");
	public static final RawAnimation ATTACK_CHARGE_END = RawAnimation.begin().thenPlay("attack.charge_end");
	public static final RawAnimation ATTACK_POWERUP = RawAnimation.begin().thenPlay("attack.powerup");

	/**
	 * A basic predicate-based {@link AnimationController} implementation.<br>
	 * Provide an {@code option A} {@link RawAnimation animation} and an {@code option B} animation, and use the predicate to determine which to play.<br>
	 * Outcome table:
	 * <pre>  true  -> Animation Option A
	 * false -> Animation Option B
	 * null  -> Stop Controller</pre>
	 */
	public static <T extends GeoAnimatable> AnimationController<T> basicPredicateController(T animatable, RawAnimation optionA, RawAnimation optionB, BiFunction<T, AnimationEvent<T>, Boolean> predicate) {
		return new AnimationController<T>(animatable, "Generic", 10, event -> {
			Boolean result = predicate.apply(animatable, event);

			if (result == null)
				return PlayState.STOP;

			if (result) {
				event.getController().setAnimation(optionA);
			}
			else {
				event.getController().setAnimation(optionB);
			}

			return PlayState.CONTINUE;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#LIVING living} controller.<br>
	 * Continuously plays the living animation
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericLivingController(T animatable) {
		return new AnimationController<>(animatable, "Living", 10, event -> {
			event.getController().setAnimation(LIVING);

			return PlayState.CONTINUE;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#IDLE idle} controller.<br>
	 * Continuously plays the idle animation
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericIdleController(T animatable) {
		return new AnimationController<T>(animatable, "Idle", 10, event -> {
			event.getController().setAnimation(IDLE);

			return PlayState.CONTINUE;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} controller.<br>
	 * Will play the walk animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericWalkController(T animatable) {
		return new AnimationController<T>(animatable, "Walk", 10, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(WALK);

				return PlayState.CONTINUE;
			}

			return PlayState.STOP;
		});
	}

	/**
	 * Generic attack controller.<br>
	 * Plays an attack animation if the animatable is {@link net.minecraft.world.entity.LivingEntity#swinging}.<br>
	 * Resets the animation each time it stops, ready for the next swing
	 * @param animatable The entity that should swing
	 * @param attackAnimation The attack animation to play (E.G. swipe, strike, stomp, swing, etc)
	 * @return A new {@link AnimationController} instance to use
	 */
	public static <T extends LivingEntity & GeoAnimatable> AnimationController<T> genericAttackAnimation(T animatable, RawAnimation attackAnimation) {
		return new AnimationController<>(animatable, "Attack", 0, event -> {
			if (animatable.swinging) {
				event.getController().setAnimation(attackAnimation);

				return PlayState.CONTINUE;
			}

			event.getController().markNeedsReload();

			return PlayState.STOP;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} + {@link DefaultAnimations#IDLE idle} controller.<br>
	 * Will play the walk animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericWalkIdleController(T animatable) {
		return new AnimationController<T>(animatable, "Walk/Idle", 10, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(WALK);
			}
			else {
				event.getController().setAnimation(IDLE);
			}

			return PlayState.CONTINUE;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#SWIM swim} controller.<br>
	 * Will play the swim animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericSwimController(T entity) {
		return new AnimationController<T>(entity, "Swim", 10, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(SWIM);

				return PlayState.CONTINUE;
			}

			return PlayState.STOP;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#SWIM swim} + {@link DefaultAnimations#IDLE idle} controller.<br>
	 * Will play the swim animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericSwimIdleController(T animatable) {
		return new AnimationController<T>(animatable, "Swim/Idle", 10, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(SWIM);
			}
			else {
				event.getController().setAnimation(IDLE);
			}

			return PlayState.CONTINUE;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#FLY walk} controller.<br>
	 * Will play the fly animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericFlyController(T animatable) {
		return new AnimationController<T>(animatable, "Fly", 10, event -> {
			event.getController().setAnimation(FLY);

			return PlayState.CONTINUE;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#FLY walk} + {@link DefaultAnimations#IDLE idle} controller.<br>
	 * Will play the walk animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericFlyIdleController(T animatable) {
		return new AnimationController<T>(animatable, "Fly/Idle", 10, event -> {
			if (event.isMoving()) {
				event.getController().setAnimation(FLY);
			}
			else {
				event.getController().setAnimation(IDLE);
			}

			return PlayState.CONTINUE;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} + {@link DefaultAnimations#RUN run} + {@link DefaultAnimations#IDLE idle} controller.<br>
	 * If the entity is considered moving, will either walk or run depending on the {@link Entity#isSprinting()} method, otherwise it will idle
	 */
	public static <T extends Entity & GeoAnimatable> AnimationController<T> genericWalkRunIdleController(T entity) {
		return new AnimationController<T>(entity, "Walk/Run/Idle", 10, event -> {
			if (event.isMoving()) {
				if (entity.isSprinting()) {
					event.getController().setAnimation(RUN);
				}
				else {
					event.getController().setAnimation(WALK);
				}
			}
			else {
				event.getController().setAnimation(IDLE);
			}

			return PlayState.CONTINUE;
		});
	}
}
