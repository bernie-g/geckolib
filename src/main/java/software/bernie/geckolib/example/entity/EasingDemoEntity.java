package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.manager.EntityAnimationManager;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib.event.ParticleKeyFrameEvent;

import javax.annotation.Nullable;
import java.util.Arrays;

public class EasingDemoEntity extends AnimalEntity implements IAnimatedEntity
{
	EntityAnimationManager collection = new EntityAnimationManager();
	AnimationController easingDemoControlller = new EntityAnimationController(this, "easingDemoController", 20, this::demoPredicate);

	private <ENTITY extends Entity> boolean demoPredicate(AnimationTestEvent<ENTITY> event)
	{
		easingDemoControlller.setAnimation(new AnimationBuilder().addAnimation("bouncetest", false));
		return true;
	}

	public EasingDemoEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		collection.addAnimationController(easingDemoControlller);
		easingDemoControlller.registerParticleListener(this::listenForParticles);
		easingDemoControlller.registerCustomInstructionListener(this::customInstructionListener);
	}

	private <ENTITY extends Entity> void customInstructionListener(CustomInstructionKeyframeEvent<ENTITY> event)
	{
		GeckoLib.LOGGER.info(Arrays.toString(event.instructions.toArray()));
	}

	private <ENTITY extends Entity> void listenForParticles(ParticleKeyFrameEvent<ENTITY> event)
	{
		GeckoLib.LOGGER.info(event.effect + " " + event.locator + " " + event);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity ageable)
	{
		return null;
	}

	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return collection;
	}
}
