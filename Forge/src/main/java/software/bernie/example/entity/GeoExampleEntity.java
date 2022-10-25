package software.bernie.example.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
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

public class GeoExampleEntity extends CreatureEntity implements IAnimatable, IAnimationTickable {
	public AnimationFactory factory = GeckoLibUtil.createFactory(this);
	private boolean isAnimating = false;

	public GeoExampleEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
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
	public ActionResultType interactAt(PlayerEntity player, Vector3d hitPos, Hand hand) {
		if (hand == Hand.MAIN_HAND) {
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
		final ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			player.displayClientMessage(new StringTextComponent("KeyFraming"), true);
		}
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		super.registerGoals();
	}

	@Override
	public int tickTimer() {
		return tickCount;
	}
}
