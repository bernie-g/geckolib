/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

public class StingrayTestEntity extends EntityMob implements IAnimatedEntity
{
	public EntityAnimationManager animationControllers = new EntityAnimationManager();
	private AnimationController wingController = new EntityAnimationController(this, "wingController", 1, this::wingAnimationPredicate);

	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return animationControllers;
	}

	public StingrayTestEntity(World worldIn)
	{
		super(worldIn);
		registerAnimationControllers();
	}

	public void registerAnimationControllers()
	{
		if(world.isRemote)
		{
			wingController.setAnimation(new AnimationBuilder().addAnimation("swimmingAnimation"));
			this.animationControllers.addAnimationController(wingController);
		}
	}

	public boolean wingAnimationPredicate(AnimationTestEvent<? extends Entity> event)
	{
		Entity entity = event.getEntity();
		World entityWorld = entity.getEntityWorld();
		if(entityWorld.rainingStrength > 0)
		{
			wingController.transitionLengthTicks = 40;
			wingController.setAnimation(new AnimationBuilder().addAnimation("thirdAnimation"));
		}
		else {
			wingController.transitionLengthTicks = 40;
			wingController.setAnimation(new AnimationBuilder().addAnimation("secondAnimation"));
		}
		return true;
	}


}
