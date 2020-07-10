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
import software.bernie.geckolib.event.SoundKeyframeEvent;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.animation.AnimationBuilder;
import software.bernie.geckolib.animation.model.AnimationController;
import software.bernie.geckolib.animation.model.AnimationControllerCollection;
import software.bernie.geckolib.animation.AnimationTestEvent;
import software.bernie.geckolib.example.KeyboardHandler;

public class TigrisEntity extends GhastEntity implements IAnimatedEntity
{
	public AnimationControllerCollection animationControllers = new AnimationControllerCollection();
	private AnimationController moveController = new AnimationController(this, "moveController", 10F, this::moveController);

	private <ENTITY extends Entity> boolean moveController(AnimationTestEvent<ENTITY> entityAnimationTestEvent)
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
	public AnimationControllerCollection getAnimationControllers()
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

	private <ENTITY extends Entity> SoundEvent flapListener(SoundKeyframeEvent<ENTITY> event)
	{
		//return whatever sound you want to play here, or return null and handle sounds yourself
		return SoundEvents.ENTITY_ENDER_DRAGON_FLAP;
	}
}
