package software.bernie.example.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GeoExampleEntity extends PathfinderMob implements IAnimatable, IAnimationTickable {
	private AnimationFactory factory = new AnimationFactory(this);

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bat.fly", true));
		return PlayState.CONTINUE;
	}

	public GeoExampleEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.noCulling = true;
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<GeoExampleEntity> controller = new AnimationController<>(this, "controller", 0, this::predicate);
		controller.registerCustomInstructionListener(this::customListener);
		data.addAnimationController(controller);
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
