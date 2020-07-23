/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.manager.EntityAnimationManager;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.event.SoundKeyframeEvent;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.example.KeyboardHandler;

public class TigrisEntity extends GhastEntity implements IAnimatedEntity
{
	public EntityAnimationManager animationControllers = new EntityAnimationManager();
	private AnimationController moveController = new EntityAnimationController(this, "moveController", 10F, this::moveController);

	private <ENTITY extends Entity> boolean moveController(AnimationTestEvent<ENTITY> entityAnimationTestEvent)
	{
		moveControl.transitionLengthTicks = 10;
		if(KeyboardHandler.isQDown)
		{
			moveControl.setAnimation(new AnimationBuilder().addAnimation("spit.fly", false).addAnimation("sit", false).addAnimation("sit", false).addAnimation("run", false).addAnimation("run", false).addAnimation("sleep", true));
		}
		else {
			moveControl.setAnimation(new AnimationBuilder().addAnimation("fly", true));
		}
		return true;
	}

	public TigrisEntity(EntityType<? extends GhastEntity> type, World worldIn)
	{
		super(type, worldIn);
		registerAnimationControllers();
	}

	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return animationControllers;
	}

	public void registerAnimationControllers()
	{
		if(world.isClient)
		{
			this.animationControllers.addAnimationController(moveControl);
			moveControl.registerSoundListener(this::flapListener);
		}
	}

	private <ENTITY extends Entity> SoundEvent flapListener(SoundKeyframeEvent<ENTITY> event)
	{
		//return whatever sound you want to play here, or return null and handle sounds yourself
		return SoundEvents.field_14550;
	}
}
