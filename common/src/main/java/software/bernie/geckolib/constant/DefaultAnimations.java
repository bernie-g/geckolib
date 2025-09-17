package software.bernie.geckolib.constant;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Function;

/**
 * Optionally usable class that holds constants for recommended animation paths
 * <p>
 * Using these won't affect much, but it may help keep some consistency in animation namings
 * <p>
 * Additionally, it encourages use of cached {@link RawAnimation RawAnimations}, to reduce overheads.
 */
public final class DefaultAnimations {
	public static final RawAnimation ITEM_ON_USE = RawAnimation.begin().thenPlay("item.use");

	public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
	public static final RawAnimation LIVING = RawAnimation.begin().thenLoop("misc.living");
	public static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("misc.spawn");
	public static final RawAnimation DIE = RawAnimation.begin().thenPlay("misc.die");
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

	public static final RawAnimation ATTACK_CAST = RawAnimation.begin().thenPlay("attack.cast");
	public static final RawAnimation ATTACK_SWING = RawAnimation.begin().thenPlay("attack.swing");
	public static final RawAnimation ATTACK_THROW = RawAnimation.begin().thenPlay("attack.throw");
	public static final RawAnimation ATTACK_PUNCH = RawAnimation.begin().thenPlay("attack.punch");
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
     * An AnimationController that does nothing, to be used for handling triggered animations that don't need to be on any other controller
     * <p>
     * This should <b><u>only</u></b> be used where you need a controller for some triggered animations, but don't want them to interfere with any other animations
     */
    public static <T extends GeoAnimatable> AnimationController<T> triggerOnlyController() {
        return new AnimationController<>("Actions", state -> PlayState.STOP);
    }

	/**
	 * A basic predicate-based {@link AnimationController} implementation
	 * <p>
	 * Return a RawAnimation based on the input {@link AnimationTest}, or null to stop the controller entirely
	 */
	public static <T extends GeoAnimatable> AnimationController<T> basicPredicateController(Function<AnimationTest<T>, @Nullable RawAnimation> predicate) {
		return new AnimationController<>("Generic", state -> {
			RawAnimation result = predicate.apply(state);

			return result == null ? PlayState.STOP : state.setAndContinue(result);
		});
	}

	/**
	 * Generic {@link DefaultAnimations#LIVING living} controller
	 * <p>
	 * Continuously plays the living animation
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericLivingController() {
		return new AnimationController<>("Living", test -> test.setAndContinue(LIVING));
	}

	/**
	 * Generic {@link DefaultAnimations#DIE die} controller
	 * <p>
	 * Plays the death animation when dying
	 */
	public static <T extends LivingEntity & GeoAnimatable> AnimationController<T> genericDeathController() {
		return new AnimationController<>("Death", test -> test.getDataOrDefault(DataTickets.IS_DEAD_OR_DYING, false) ? test.setAndContinue(DIE) : PlayState.STOP);
	}

	/**
	 * Generic {@link DefaultAnimations#IDLE idle} controller
	 * <p>
	 * Continuously plays the idle animation
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericIdleController() {
		return new AnimationController<T>("Idle", test -> test.setAndContinue(IDLE));
	}

	/**
	 * Generic {@link DefaultAnimations#SPAWN spawn} controller
	 * <p>
	 * Plays the spawn animation as long as the current {@link GeoAnimatable#getTick tick} of the animatable is {@literal <=} the value provided in {@code ticks}
	 * <p>
	 * For the {@code objectSupplier}, provide the relevant object for the animatable being animated
	 * </p>
	 * Recommended:
	 * <ul>
	 *     <li>{@link software.bernie.geckolib.animatable.GeoEntity GeoEntity}: state -> animatable</li>
	 *     <li>{@link software.bernie.geckolib.animatable.GeoBlockEntity GeoBlockEntity}: state -> animatable</li>
	 *     <li>{@link software.bernie.geckolib.animatable.GeoReplacedEntity GeoReplacedEntity}: state -> state.getData(DataTickets.ENTITY)</li>
	 *     <li>{@link software.bernie.geckolib.animatable.GeoItem GeoItem}: state -> state.getData(DataTickets.ITEMSTACK)</li>
	 *     <li>{@code GeoArmor}: state -> state.getData(DataTickets.ENTITY)</li>
	 * </ul>
	 *
	 * @param spawnTicks The number of ticks the animation should run for. After this value is surpassed, the animation will no longer play
	 */
	public static <T extends GeoAnimatable> AnimationController<T> getSpawnController(int spawnTicks) {
		return new AnimationController<>("Spawn", test ->
				test.getData(DataTickets.TICK) <= spawnTicks ? test.setAndContinue(DefaultAnimations.SPAWN) : PlayState.STOP);
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} controller
	 * <p>
	 * Will play the walk animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericWalkController() {
		return new AnimationController<>("Walk", test -> test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP);
	}

	/**
	 * Generic attack controller
	 * <p>
	 * Plays an attack animation if the animatable is {@link net.minecraft.world.entity.LivingEntity#swinging}
	 * <p>
	 * Resets the animation each time it stops, ready for the next swing
	 *
	 * @param attackAnimation The attack animation to play (E.G. swipe, strike, stomp, swing, etc)
	 * @return A new {@link AnimationController} instance to use
	 */
	public static <T extends LivingEntity & GeoAnimatable> AnimationController<T> genericAttackAnimation(RawAnimation attackAnimation) {
		return new AnimationController<>("Attack", test -> {
			if (test.getDataOrDefault(DataTickets.SWINGING_ARM, false))
				return test.setAndContinue(attackAnimation);

			test.controller().forceAnimationReset();

			return PlayState.STOP;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} + {@link DefaultAnimations#IDLE idle} controller
	 * <p>
	 * Will play the walk animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericWalkIdleController() {
		return new AnimationController<>("Walk/Idle", test -> test.setAndContinue(test.isMoving() ? WALK : IDLE));
	}

	/**
	 * Generic {@link DefaultAnimations#SWIM swim} controller
	 * <p>
	 * Will play the swim animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericSwimController() {
		return new AnimationController<>("Swim", test -> test.isMoving() ? test.setAndContinue(SWIM) : PlayState.STOP);
	}

	/**
	 * Generic {@link DefaultAnimations#SWIM swim} + {@link DefaultAnimations#IDLE idle} controller
	 * <p>
	 * Will play the swim animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericSwimIdleController() {
		return new AnimationController<>("Swim/Idle", test -> test.setAndContinue(test.isMoving() ? SWIM : IDLE));
	}

	/**
	 * Generic {@link DefaultAnimations#FLY walk} controller
	 * <p>
	 * Will play the fly animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericFlyController() {
		return new AnimationController<>("Fly", test -> test.isMoving() ? test.setAndContinue(FLY) : PlayState.STOP);
	}

	/**
	 * Generic {@link DefaultAnimations#FLY walk} + {@link DefaultAnimations#IDLE idle} controller
	 * <p>
	 * Will play the walk animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericFlyIdleController() {
		return new AnimationController<>("Fly/Idle", test -> test.setAndContinue(test.isMoving() ? FLY : IDLE));
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} + {@link DefaultAnimations#RUN run} + {@link DefaultAnimations#IDLE idle} controller
	 * <p>
	 * If the entity is considered moving, will either walk or run depending on the {@link Entity#isSprinting()} method, otherwise it will idle
	 */
	public static <T extends Entity & GeoAnimatable> AnimationController<T> genericWalkRunIdleController() {
		return new AnimationController<>("Walk/Run/Idle", test -> {
			if (test.isMoving())
				return test.setAndContinue(test.getDataOrDefault(DataTickets.SPRINTING, false) ? RUN : WALK);

			return test.setAndContinue(IDLE);
		});
	}
}
