/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.event.SoundKeyframeEvent;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.example.KeyboardHandler;

public class TigrisEntity extends GhastEntity implements IAnimatable
{
	public AnimationManager animationControllers = new AnimationManager();
	private AnimationController moveController = new EntityAnimationController(this, "moveController", 10F, this::moveController);

	private <ENTITY extends Entity & IAnimatable> boolean moveController(AnimationTestPredicate<ENTITY> entityAnimationTestPredicate)
	{
		moveController.transitionLengthTicks = 10;
		if(KeyboardHandler.isQDown)
		{
			moveController.setAnimation(new AnimationBuilder().addAnimation("spit.fly", false).addAnimation("sit", false).addAnimation("sit", false).addAnimation("run", false).addAnimation("run", false).addAnimation("sleep", true));
		}
		else {
			moveController.setAnimation(new AnimationBuilder().addAnimation("fly", true));
		}
		return true;
	}

	public TigrisEntity(EntityType<? extends GhastEntity> type, World worldIn)
	{
		super(type, worldIn);
		registerAnimationControllers();
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return animationControllers;
	}

	public void registerAnimationControllers()
	{
		if(world.isRemote)
		{
			this.animationControllers.addAnimationController(moveController);
			moveController.registerSoundListener(this::flapListener);
		}
	}


	private <ENTITY extends IAnimatable> SoundEvent flapListener(SoundKeyframeEvent<ENTITY> event)
	{
		//return whatever sound you want to play here, or return null and handle sounds yourself
		return SoundEvents.ENTITY_ENDER_DRAGON_FLAP;
	}
}
