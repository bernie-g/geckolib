package software.bernie.example.entity;

import java.util.Optional;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.Packet;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import software.bernie.example.ClientListener.EntityPacket;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class ExtendedRendererEntity extends PathAwareEntity implements IAnimatable {

	// Geckolib
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public ExtendedRendererEntity(EntityType<? extends PathAwareEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F /* distance */));
	}

	public boolean isTwoHandedAnimationRunning() {
		return this.isSpellCasting() || this.isSpinToWinActive() || this.isWieldingTwoHandedWeapon();
	}

	public boolean isSpinToWinActive() {
		return false;
	}

	public boolean isSpellCasting() {
		return false;
	}

	public boolean isWieldingTwoHandedWeapon() {
		return
		// Bow and crossbows
		(this.getMainHandStack().getItem() instanceof RangedWeaponItem
				|| this.getOffHandStack().getItem() instanceof RangedWeaponItem
				|| this.getMainHandStack().getUseAction() == UseAction.BOW
				|| this.getOffHandStack().getUseAction() == UseAction.BOW
				|| this.getMainHandStack().getUseAction() == UseAction.CROSSBOW
				|| this.getOffHandStack().getUseAction() == UseAction.CROSSBOW)
				|| (this.getMainHandStack().getUseAction() == UseAction.SPEAR
						|| this.getOffHandStack().getUseAction() == UseAction.SPEAR);
	}

	@Override
	public void registerControllers(AnimationData data) {
		// Idle
		data.addAnimationController(new AnimationController<>(this, "controller_idle", 0, this::predicateIdle));
		// Body pose
		data.addAnimationController(
				new AnimationController<>(this, "controller_body_pose", 20, this::predicateBodyPose));
		// Arms
		data.addAnimationController(
				new AnimationController<>(this, "controller_left_hand_pose", 10, this::predicateLeftArmPose));
		data.addAnimationController(
				new AnimationController<>(this, "controller_right_hand_pose", 10, this::predicateRightArmPose));
		data.addAnimationController(
				new AnimationController<>(this, "controller_left_hand", 0, this::predicateLeftArmSwing));
		data.addAnimationController(
				new AnimationController<>(this, "controller_right_hand", 0, this::predicateRightArmSwing));
		// TwoHanded
		data.addAnimationController(
				new AnimationController<>(this, "controller_twohanded_pose", 10, this::predicateTwoHandedPose));
		data.addAnimationController(
				new AnimationController<>(this, "controller_twohanded", 10, this::predicateTwoHandedSwing));
		// Spin hands
		// data.addAnimationController(new AnimationController<>(this,
		// "controller_spin_hands", 0, this::predicateSpinHands));
	}

	private static final String ANIM_NAME_PREFIX = "animation.biped.";

	private static final String ANIM_NAME_SPIN_HANDS = ANIM_NAME_PREFIX + "spin_hands";

	@SuppressWarnings("unused")
	private <E extends IAnimatable> PlayState predicateSpinHands(AnimationEvent<E> event) {
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SPIN_HANDS, EDefaultLoopTypes.LOOP));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_IDLE = ANIM_NAME_PREFIX + "idle";

	private <E extends IAnimatable> PlayState predicateIdle(AnimationEvent<E> event) {
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_IDLE, EDefaultLoopTypes.LOOP));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_SITTING = ANIM_NAME_PREFIX + "sit";
	private static final String ANIM_NAME_SNEAKING = ANIM_NAME_PREFIX + "body.sneak";

	private <E extends IAnimatable> PlayState predicateBodyPose(AnimationEvent<E> event) {
		if (this.isTwoHandedAnimationRunning()) {

		} else if (this.hasVehicle()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SITTING, EDefaultLoopTypes.LOOP));
			return PlayState.CONTINUE;
		} else if (this.isInSneakingPose()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SNEAKING, EDefaultLoopTypes.LOOP));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private static final String ANIM_NAME_BLOCKING_LEFT = ANIM_NAME_PREFIX + "arms.left.block";
	private static final String ANIM_NAME_BLOCKING_RIGHT = ANIM_NAME_PREFIX + "arms.right.block";

	protected Hand getLeftHand() {
		return this.isLeftHanded() ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	protected Hand getRightHand() {
		return !this.isLeftHanded() ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	private <E extends IAnimatable> PlayState predicateRightArmSwing(AnimationEvent<E> event) {
		return this.predicateHandSwing(this.getRightHand(), false, event);
	}

	private <E extends IAnimatable> PlayState predicateLeftArmSwing(AnimationEvent<E> event) {
		return this.predicateHandSwing(this.getLeftHand(), true, event);
	}

	protected <E extends IAnimatable> PlayState predicateHandSwing(Hand hand, boolean leftHand,
			AnimationEvent<E> event) {
		if (this.handSwinging && !this.isTwoHandedAnimationRunning()) {
			ItemStack handItemStack = this.getStackInHand(hand);
			if (!handItemStack.isEmpty()) {
				if (handItemStack.getItem().getUseAction(handItemStack) == UseAction.EAT
						|| handItemStack.getItem().getUseAction(handItemStack) == UseAction.DRINK) {
					// Eating/Drinking animation
				} else {
					// Normal swinging
				}
				return PlayState.CONTINUE;
			}
		}
		return PlayState.STOP;
	}

	private <E extends IAnimatable> PlayState predicateRightArmPose(AnimationEvent<E> event) {
		return this.predicateHandPose(this.getRightHand(), false, event);
	}

	private <E extends IAnimatable> PlayState predicateLeftArmPose(AnimationEvent<E> event) {
		return this.predicateHandPose(this.getLeftHand(), true, event);
	}

	protected <E extends IAnimatable> PlayState predicateHandPose(Hand hand, boolean leftHand,
			AnimationEvent<E> event) {
		ItemStack handItemStack = this.getStackInHand(hand);
		if (!handItemStack.isEmpty() && !this.isTwoHandedAnimationRunning()) {
			Item handItem = handItemStack.getItem();
			if (this.isBlocking()
					&& (handItem instanceof ShieldItem || handItem.getUseAction(handItemStack) == UseAction.BLOCK)) {
				event.getController().setAnimation(new AnimationBuilder()
						.addAnimation(leftHand ? ANIM_NAME_BLOCKING_LEFT : ANIM_NAME_BLOCKING_RIGHT, EDefaultLoopTypes.LOOP));
			} else {
				// If the item is a small gun play the correct animation
			}
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private static final String ANIM_NAME_SPELLCASTING = ANIM_NAME_PREFIX + "arms.cast-spell";

	private <E extends IAnimatable> PlayState predicateTwoHandedPose(AnimationEvent<E> event) {
		if (this.isTwoHandedAnimationRunning()) {
			if (this.isSpellCasting()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SPELLCASTING, EDefaultLoopTypes.LOOP));
				return PlayState.CONTINUE;
			} else {
				// First: Check for firearm, spear and greatsword in either hand
				// Main hand has priority
				Optional<PlayState> resultState = performTwoHandedLogicPerHand(this.getMainHandStack(),
						this.isLeftHanded(), event);
				if (!resultState.isPresent()) {
					resultState = performTwoHandedLogicPerHand(this.getOffHandStack(), !this.isLeftHanded(), event);
				}
				if (resultState.isPresent()) {
					return resultState.get();
				}
			}
		}
		return PlayState.STOP;
	}

	private static final String ANIM_NAME_SPEAR_POSE_LEFT = ANIM_NAME_PREFIX + "arms.left.spear";
	private static final String ANIM_NAME_SPEAR_POSE_RIGHT = ANIM_NAME_PREFIX + "arms.right.spear";

	private static final String ANIM_NAME_FIREARM_POSE_LEFT = ANIM_NAME_PREFIX + "arms.left.firearm";
	private static final String ANIM_NAME_FIREARM_POSE_RIGHT = ANIM_NAME_PREFIX + "arms.right.firearm";

	@SuppressWarnings("unused")
	private static final String ANIM_NAME_GREATSWORD_POSE = ANIM_NAME_PREFIX + "arms.greatsword";

	private <E extends IAnimatable> Optional<PlayState> performTwoHandedLogicPerHand(ItemStack itemStack,
			boolean leftHanded, AnimationEvent<E> event) {
		if (itemStack.isEmpty()) {
			return Optional.empty();
		}
		Item item = itemStack.getItem();
		// If item instanceof ItemGreatsword => Greatsword animation
		// If item instanceof Spear => spear animation
		// If item instanceof Firearm/Bow/Crossbow => firearm animation
		if (item.getUseAction(itemStack) == UseAction.BOW || item.getUseAction(itemStack) == UseAction.CROSSBOW) {
			// Firearm
			event.getController().setAnimation(new AnimationBuilder()
					.addAnimation(leftHanded ? ANIM_NAME_FIREARM_POSE_LEFT : ANIM_NAME_FIREARM_POSE_RIGHT, EDefaultLoopTypes.LOOP));
			return Optional.of(PlayState.CONTINUE);
		} else if (item.getUseAction(itemStack) == UseAction.SPEAR) {
			// Yes this is for tridents but we can use it anyway
			// Spear
			event.getController().setAnimation(new AnimationBuilder()
					.addAnimation(leftHanded ? ANIM_NAME_SPEAR_POSE_LEFT : ANIM_NAME_SPEAR_POSE_RIGHT, EDefaultLoopTypes.LOOP));
			return Optional.of(PlayState.CONTINUE);
		}
		// If item is greatsword => greatsword animation
		return Optional.empty();
	}

	@SuppressWarnings("unused")
	private static final String ANIM_NAME_GREATSWORD_SWING = ANIM_NAME_PREFIX + "arms.attack-greatsword";
	private static final String ANIM_NAME_SPEAR_SWING = ANIM_NAME_PREFIX + "arms.attack-spear";

	private <E extends IAnimatable> PlayState predicateTwoHandedSwing(AnimationEvent<E> event) {
		if (this.isTwoHandedAnimationRunning() && this.handSwinging) {
			// Check for greatsword & spear and play their animations
			if (this.getMainHandStack().getItem().getUseAction(this.getMainHandStack()) == UseAction.SPEAR
					|| this.getOffHandStack().getItem().getUseAction(this.getOffHandStack()) == UseAction.SPEAR) {
				// Spear use animation
				event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SPEAR_SWING, EDefaultLoopTypes.PLAY_ONCE));
			}
			// If either hand item is greatsword => greatsword animation
		}
		return PlayState.STOP;
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return EntityPacket.createPacket(this);
	}

	@Override
	protected ActionResult interactMob(PlayerEntity pPlayer, Hand pHand) {
		ItemStack item = pPlayer.getStackInHand(pHand);
		if (item != null && !item.isEmpty() && !this.world.isClient()) {
			if (item.getItem() instanceof ArmorItem) {
				ArmorItem ai = (ArmorItem) item.getItem();
				this.equipStack(ai.getSlotType(), item);
			} else if (item.getItem() instanceof BlockItem
					&& ((BlockItem) item.getItem()).getBlock() instanceof AbstractSkullBlock) {
				this.equipStack(EquipmentSlot.HEAD, item);
			} else {
				this.setStackInHand(pHand, item);
			}
			pPlayer.sendSystemMessage(new LiteralText("Equipped item: " + item.getItem().getTranslationKey() + "!"),
					this.getUuid());
			return ActionResult.SUCCESS;
		}
		return super.interactMob(pPlayer, pHand);
	}

}
