/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.SoundEvent;
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
		moveControl.transitionLength = 10;
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
	public AnimationControllerCollection getAnimationControllers()
	{
		return animationControllers;
	}

	public void registerAnimationControllers()
	{
		if(world.isClient)
		{
			this.animationControllers.addAnimationController(moveControl);
			moveControl.registerSoundListener(this::moveSoundListener);
		}
	}

	private <ENTITY extends Entity> void moveSoundListener(SoundEvent<ENTITY> entitySoundEvent)
	{
		GeckoLib.LOGGER.info(entitySoundEvent.sound);
	}
}
