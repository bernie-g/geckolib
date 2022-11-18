package software.bernie.example.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.animatable.GeoEntity;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.util.ClientUtils;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example {@link GeoAnimatable} implementation of an entity
 * @see software.bernie.example.client.renderer.entity.BatRenderer
 * @see software.bernie.example.client.model.entity.BatModel
 */
public class BatEntity extends PathfinderMob implements GeoEntity {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	private boolean isFlying = false;

	public BatEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	// Have the bat look at the player
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
		super.registerGoals();
	}

	// Adds a right-click toggle that turns on/off its animating pose
	@Override
	public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
		if (hand == InteractionHand.MAIN_HAND)
			this.isFlying = !this.isFlying;

		return super.interactAt(player, hitPos, hand);
	}

	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		// Add our flying animation controller
		manager.addController(new AnimationController<>(this, event -> {
			if (this.isFlying) {
				event.getController().setAnimation(DefaultAnimations.FLY);
			}
			else {
				event.getController().setAnimation(DefaultAnimations.IDLE);
			}

			return PlayState.CONTINUE;
			// Handle the custom instruction keyframe that is part of our animation json
		}).setCustomInstructionKeyframeHandler(event -> {
			Player player = ClientUtils.getClientPlayer();

			if (player != null)
				player.displayClientMessage(Component.literal("KeyFraming"), true);
		}));

		// Add our generic living animation controller
		manager.addController(DefaultAnimations.genericLivingController(this));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
