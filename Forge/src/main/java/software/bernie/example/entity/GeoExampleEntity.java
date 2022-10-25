package software.bernie.example.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class GeoExampleEntity extends PathfinderMob implements IAnimatable, IAnimationTickable {
	public AnimationFactory factory = GeckoLibUtil.createFactory(this);
	private boolean isAnimating = false;

	public GeoExampleEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		if (this.isAnimating) {
			event.getController()
					.setAnimation(new AnimationBuilder().addAnimation("animation.bat.fly", EDefaultLoopTypes.PLAY_ONCE)
							.addAnimation("animation.bat.idle", EDefaultLoopTypes.PLAY_ONCE));
		} else {
			event.getController().clearAnimationCache();
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	private <E extends IAnimatable> PlayState predicateSpin(AnimationEvent<E> event) {
		event.getController()
				.setAnimation(new AnimationBuilder().addAnimation("animation.bat.spin", EDefaultLoopTypes.LOOP));
		return PlayState.CONTINUE;
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
		if (hand == InteractionHand.MAIN_HAND) {
			this.isAnimating = !this.isAnimating;
		}
		return super.interactAt(player, hitPos, hand);
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<GeoExampleEntity> controller = new AnimationController<>(this, "controller", 0,
				this::predicate);
		AnimationController<GeoExampleEntity> controllerspin = new AnimationController<>(this, "controllerspin", 0,
				this::predicateSpin);
		controller.registerCustomInstructionListener(this::customListener);
		data.addAnimationController(controller);
		data.addAnimationController(controllerspin);
	}

	private <ENTITY extends IAnimatable> void customListener(CustomInstructionKeyframeEvent<ENTITY> event) {
		final LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.displayClientMessage(new TextComponent("KeyFraming"), true);
		}
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		super.registerGoals();
	}

	@Override
	public int tickTimer() {
		return tickCount;
	}
}
