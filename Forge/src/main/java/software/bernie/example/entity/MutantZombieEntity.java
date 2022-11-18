package software.bernie.example.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import software.bernie.example.client.renderer.entity.MutantZombieRenderer;
import software.bernie.geckolib3.animatable.GeoEntity;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.core.animation.RawAnimation;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example extended-support entity for GeckoLib advanced rendering
 * @see software.bernie.geckolib3.renderer.ExtendedGeoEntityRenderer
 * @see MutantZombieRenderer
 */
public class MutantZombieEntity extends PathfinderMob implements GeoEntity {
	// Pre-define our RawAnimations for use later
	private static final RawAnimation BLOCK_LEFT = RawAnimation.begin().thenPlay("attack.block.left");
	private static final RawAnimation BLOCK_RIGHT = RawAnimation.begin().thenPlay("attack.block.right");
	private static final RawAnimation AIM_LEFT_HAND = RawAnimation.begin().thenPlay("pose.aim.left");
	private static final RawAnimation AIM_RIGHT_HAND = RawAnimation.begin().thenPlay("pose.aim.right");
	private static final RawAnimation SPEAR_LEFT_HAND = RawAnimation.begin().thenPlay("pose.spear.left");
	private static final RawAnimation SPEAR_RIGHT_HAND = RawAnimation.begin().thenPlay("pose.spear.right");
	private static final RawAnimation INTERACT_LEFT = RawAnimation.begin().thenPlay("misc.interact.right");
	private static final RawAnimation INTERACT_RIGHT = RawAnimation.begin().thenPlay("misc.interact.right");
	private static final RawAnimation SPEAR_SWING = RawAnimation.begin().thenPlay("attack.spear");

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public MutantZombieEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8));
	}

	// Let's add our animation controllers
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		// A generic idle controller
		manager.addController(DefaultAnimations.genericIdleController(this))
				.addController(new AnimationController<>(this, "Body", 20, this::poseBody))
				.addController(new AnimationController<>(this, "Left Hand", 10, event -> predicateHandPose(getLeftHand(), event))
						.triggerableAnim("interact", INTERACT_LEFT))
				.addController(new AnimationController<>(this, "Right Hand", 10, event -> predicateHandPose(getRightHand(), event))
						.triggerableAnim("interact", INTERACT_RIGHT))
				.addController(new AnimationController<>(this, "Dual Wield Pose", 10, this::poseDualWield))
				.addController(new AnimationController<>(this, "Dual Wield Attack", 10, this::attackDualWield));
	}

	// Create the animation handler for the body segment
	protected PlayState poseBody(AnimationEvent<MutantZombieEntity> event) {
		if (isWieldingTwoHandedWeapon())
			return PlayState.STOP;

		if (isPassenger()) {
			event.getController().setAnimation(DefaultAnimations.SIT);
		}
		else if (isCrouching()) {
			event.getController().setAnimation(DefaultAnimations.SNEAK);
		}

		return PlayState.CONTINUE;
	}

	// Create the animation handler for each hand
	protected PlayState predicateHandPose(InteractionHand hand, AnimationEvent<MutantZombieEntity> event) {
		ItemStack heldStack = getItemInHand(hand);

		if (heldStack.isEmpty() || isWieldingTwoHandedWeapon())
			return PlayState.STOP;

		Item handItem = heldStack.getItem();

		if (isBlocking() && (handItem instanceof ShieldItem || handItem.getUseAnimation(heldStack) == UseAnim.BLOCK)) {
			event.getController().setAnimation(getLeftHand() == hand ? BLOCK_LEFT : BLOCK_RIGHT);

			return PlayState.CONTINUE;
		}

		return PlayState.STOP;
	}

	// Create the animation handler for posing with a dual-wielded weapon
	private  PlayState poseDualWield(AnimationEvent<MutantZombieEntity> event) {
		if (!isWieldingTwoHandedWeapon())
			return PlayState.STOP;

		for (ItemStack heldStack : getHandSlots()) {
			UseAnim useAnim = heldStack.getItem().getUseAnimation(heldStack);

			if (useAnim == UseAnim.BOW || useAnim == UseAnim.CROSSBOW) {
				event.getController().setAnimation(isLeftHanded() ? AIM_LEFT_HAND : AIM_RIGHT_HAND);

				return PlayState.CONTINUE;
			}
			else if (useAnim == UseAnim.SPEAR) {
				event.getController().setAnimation(isLeftHanded() ? SPEAR_LEFT_HAND : SPEAR_RIGHT_HAND);

				return PlayState.CONTINUE;
			}
		}

		return PlayState.STOP;
	}

	// Create the animation handler for attacking with a dual-wielded weapon
	private <E extends GeoAnimatable> PlayState attackDualWield(AnimationEvent<E> event) {
		if (!this.swinging || !isWieldingTwoHandedWeapon())
			return PlayState.STOP;

		for (ItemStack heldStack : getHandSlots()) {
			if (heldStack.getItem().getUseAnimation(heldStack) == UseAnim.SPEAR) {
				event.getController().setAnimation(SPEAR_SWING);

				return PlayState.CONTINUE;
			}
		}

		return PlayState.STOP;
	}

	// Helper method to determine whether the entity should be considered to be dual-wielding or not
	public boolean isWieldingTwoHandedWeapon() {
		ItemStack mainHandStack = getMainHandItem();
		ItemStack offhandStack = getOffhandItem();

		if (mainHandStack.getItem() instanceof ProjectileWeaponItem ||
				offhandStack.getItem() instanceof ProjectileWeaponItem)
			return true;

		UseAnim anim = mainHandStack.getUseAnimation();

		if (anim == UseAnim.BOW || anim == UseAnim.CROSSBOW || anim == UseAnim.SPEAR)
			return true;

		anim = offhandStack.getUseAnimation();

		return anim == UseAnim.BOW || anim == UseAnim.CROSSBOW || anim == UseAnim.SPEAR;
	}

	// Helper method to get the left hand in an ambidextrous entity
	protected InteractionHand getLeftHand() {
		return this.isLeftHanded() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}

	// Helper method to get the right hand in an ambidextrous entity
	protected InteractionHand getRightHand() {
		return !this.isLeftHanded() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}

	// Add a functionality to give the entity whatever we're holding when we click on it
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (this.level.isClientSide() || stack.isEmpty())
			return super.mobInteract(player, hand);

		setItemSlot(LivingEntity.getEquipmentSlotForItem(stack), stack.copy());
		player.sendSystemMessage(Component.translatable("entity.geckolib3.example_extended_entity.equip", stack.getDisplayName()));
		triggerAnim(getLeftHand() == hand ? "Left Hand" : "Right Hand", "interact");

		return InteractionResult.SUCCESS;
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}