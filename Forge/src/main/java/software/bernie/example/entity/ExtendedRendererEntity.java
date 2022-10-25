package software.bernie.example.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Optional;

public class ExtendedRendererEntity extends PathfinderMob implements IAnimatable {

	// Geckolib
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public ExtendedRendererEntity(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
		super(p_i48575_1_, p_i48575_2_);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8.0F /* distance */));
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
		(this.getMainHandItem().getItem() instanceof ProjectileWeaponItem
				|| this.getOffhandItem().getItem() instanceof ProjectileWeaponItem
				|| this.getMainHandItem().getUseAnimation() == UseAnim.BOW
				|| this.getOffhandItem().getUseAnimation() == UseAnim.BOW
				|| this.getMainHandItem().getUseAnimation() == UseAnim.CROSSBOW
				|| this.getOffhandItem().getUseAnimation() == UseAnim.CROSSBOW)
				|| (this.getMainHandItem().getUseAnimation() == UseAnim.SPEAR
						|| this.getOffhandItem().getUseAnimation() == UseAnim.SPEAR);
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
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SPIN_HANDS, true));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_IDLE = ANIM_NAME_PREFIX + "idle";

	private <E extends IAnimatable> PlayState predicateIdle(AnimationEvent<E> event) {
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_IDLE, true));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_SITTING = ANIM_NAME_PREFIX + "sit";
	private static final String ANIM_NAME_SNEAKING = ANIM_NAME_PREFIX + "body.sneak";

	private <E extends IAnimatable> PlayState predicateBodyPose(AnimationEvent<E> event) {
		if (this.isTwoHandedAnimationRunning()) {

		} else if (this.isPassenger()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SITTING, true));
			return PlayState.CONTINUE;
		} else if (this.isCrouching()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SNEAKING, true));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private static final String ANIM_NAME_BLOCKING_LEFT = ANIM_NAME_PREFIX + "arms.left.block";
	private static final String ANIM_NAME_BLOCKING_RIGHT = ANIM_NAME_PREFIX + "arms.right.block";

	protected InteractionHand getLeftHand() {
		return this.isLeftHanded() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}

	protected InteractionHand getRightHand() {
		return !this.isLeftHanded() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}

	private <E extends IAnimatable> PlayState predicateRightArmSwing(AnimationEvent<E> event) {
		return this.predicateHandSwing(this.getRightHand(), false, event);
	}

	private <E extends IAnimatable> PlayState predicateLeftArmSwing(AnimationEvent<E> event) {
		return this.predicateHandSwing(this.getLeftHand(), true, event);
	}

	protected <E extends IAnimatable> PlayState predicateHandSwing(InteractionHand hand, boolean leftHand,
			AnimationEvent<E> event) {
		if (this.swinging && !this.isTwoHandedAnimationRunning()) {
			ItemStack handItemStack = this.getItemInHand(hand);
			if (!handItemStack.isEmpty()) {
				if (handItemStack.getItem().getUseAnimation(handItemStack) == UseAnim.EAT
						|| handItemStack.getItem().getUseAnimation(handItemStack) == UseAnim.DRINK) {
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

	protected <E extends IAnimatable> PlayState predicateHandPose(InteractionHand hand, boolean leftHand,
			AnimationEvent<E> event) {
		ItemStack handItemStack = this.getItemInHand(hand);
		if (!handItemStack.isEmpty() && !this.isTwoHandedAnimationRunning()) {
			Item handItem = handItemStack.getItem();
			if (this.isBlocking()
					&& (handItem instanceof ShieldItem || handItem.getUseAnimation(handItemStack) == UseAnim.BLOCK)) {
				event.getController().setAnimation(new AnimationBuilder()
						.addAnimation(leftHand ? ANIM_NAME_BLOCKING_LEFT : ANIM_NAME_BLOCKING_RIGHT, true));
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
				event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SPELLCASTING, true));
				return PlayState.CONTINUE;
			} else {
				// First: Check for firearm, spear and greatsword in either hand
				// Main hand has priority
				Optional<PlayState> resultState = performTwoHandedLogicPerHand(this.getMainHandItem(),
						this.isLeftHanded(), event);
				if (!resultState.isPresent()) {
					resultState = performTwoHandedLogicPerHand(this.getOffhandItem(), !this.isLeftHanded(), event);
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
		if (item.getUseAnimation(itemStack) == UseAnim.BOW || item.getUseAnimation(itemStack) == UseAnim.CROSSBOW) {
			// Firearm
			event.getController().setAnimation(new AnimationBuilder()
					.addAnimation(leftHanded ? ANIM_NAME_FIREARM_POSE_LEFT : ANIM_NAME_FIREARM_POSE_RIGHT, true));
			return Optional.of(PlayState.CONTINUE);
		} else if (item.getUseAnimation(itemStack) == UseAnim.SPEAR) {
			// Yes this is for tridents but we can use it anyway
			// Spear
			event.getController().setAnimation(new AnimationBuilder()
					.addAnimation(leftHanded ? ANIM_NAME_SPEAR_POSE_LEFT : ANIM_NAME_SPEAR_POSE_RIGHT, true));
			return Optional.of(PlayState.CONTINUE);
		}
		// If item is greatsword => greatsword animation
		return Optional.empty();
	}

	@SuppressWarnings("unused")
	private static final String ANIM_NAME_GREATSWORD_SWING = ANIM_NAME_PREFIX + "arms.attack-greatsword";
	private static final String ANIM_NAME_SPEAR_SWING = ANIM_NAME_PREFIX + "arms.attack-spear";

	private <E extends IAnimatable> PlayState predicateTwoHandedSwing(AnimationEvent<E> event) {
		if (this.isTwoHandedAnimationRunning() && this.swinging) {
			// Check for greatsword & spear and play their animations
			if (this.getMainHandItem().getItem().getUseAnimation(this.getMainHandItem()) == UseAnim.SPEAR
					|| this.getOffhandItem().getItem().getUseAnimation(this.getOffhandItem()) == UseAnim.SPEAR) {
				// Spear use animation
				event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SPEAR_SWING, false));
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
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		ItemStack item = pPlayer.getItemInHand(pHand);
		if (item != null && !item.isEmpty() && !this.level.isClientSide) {
			if (item.getItem() instanceof ArmorItem) {
				ArmorItem ai = (ArmorItem) item.getItem();
				this.setItemSlot(ai.getSlot(), item);
			} else if (item.getItem().getEquipmentSlot(item) != null) {
				this.setItemSlot(item.getItem().getEquipmentSlot(item), item);
			} else if (item.getItem() instanceof BlockItem
					&& ((BlockItem) item.getItem()).getBlock() instanceof AbstractSkullBlock) {
				this.setItemSlot(EquipmentSlot.HEAD, item);
			} else {
				this.setItemInHand(pHand, item);
			}
			pPlayer.sendSystemMessage(Component
					.literal("Equipped item: " + ForgeRegistries.ITEMS.getKey(item.getItem()).toString() + "!"));
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(pPlayer, pHand);
	}

}
