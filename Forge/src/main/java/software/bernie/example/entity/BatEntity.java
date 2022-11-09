package software.bernie.example.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
import software.bernie.geckolib3.core.animation.AnimationData;
import software.bernie.geckolib3.core.animation.RawAnimation;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.keyframe.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example {@link GeoAnimatable} implementation of an entity
 * @see software.bernie.example.client.renderer.entity.BatRenderer
 * @see software.bernie.example.client.model.entity.BatModel
 */
public class BatEntity extends PathfinderMob implements GeoEntity {
	private static final RawAnimation FLYING_ANIM = RawAnimation.begin().thenPlay("move.fly").thenPlay("misc.idle");

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	private boolean isAnimating = false;

	public BatEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	// Have the bat look at the player
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		super.registerGoals();
	}

	// Adds a right-click toggle that turns on/off its animating pose
	@Override
	public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
		if (hand == InteractionHand.MAIN_HAND)
			this.isAnimating = !this.isAnimating;

		return super.interactAt(player, hitPos, hand);
	}

	@Override
	public void registerControllers(AnimationData<?> data) {
		// Add our flying animation controller
		data.addAnimationController(new AnimationController<>(this, event -> {
			if (this.isAnimating) {
				event.getController().setAnimation(FLYING_ANIM);

				return PlayState.CONTINUE;
			}
			else {
				// Reset the animation so the next time it starts, we start from the first animation
				event.getController().markNeedsReload();

				return PlayState.STOP;
			}
			// Handle the custom instruction keyframe that is part of our animation json, in a new class instance for server-safety
		}).setCustomInstructionKeyframeHandler(new AnimationController.CustomKeyframeHandler<BatEntity>() {
			@Override
			public void handle(CustomInstructionKeyframeEvent<BatEntity> event) {
				final LocalPlayer player = Minecraft.getInstance().player;

				if (player != null)
					player.displayClientMessage(Component.literal("KeyFraming"), true);
			}
		}));

		// Add our generic living animation controller
		data.addAnimationController(DefaultAnimations.genericLivingController(this));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
