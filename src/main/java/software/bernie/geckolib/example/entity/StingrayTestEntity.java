/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.entity;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.manager.EntityAnimationManager;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.easing.EasingType;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;

public class StingrayTestEntity extends WaterCreatureEntity implements IAnimatedEntity
{
	public EntityAnimationManager animationControllers = new EntityAnimationManager();
	private AnimationController wingController = new EntityAnimationController(this, "wingController", 1, this::wingAnimationPredicate);

	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return animationControllers;
	}

	public StingrayTestEntity(EntityType<? extends WaterCreatureEntity> p_i48565_1_, World p_i48565_2_)
	{
		super(p_i48565_1_, p_i48565_2_);
		this.registerAnimationControllers();

	}

	public void registerAnimationControllers()
	{
		if(world.isClient)
		{
			wingController.setAnimation(new AnimationBuilder().addAnimation("swimmingAnimation"));
			this.animationControllers.addAnimationController(wingController);
		}
	}

	<E extends Entity> boolean wingAnimationPredicate(AnimationTestEvent<E> event)
	{
		Entity entity = event.getEntity();
		ClientWorld entityWorld = (ClientWorld) entity.getEntityWorld();
		wingController.easingType = EasingType.EaseInOutQuart;
		if(entityWorld.rainGradient > 0)
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
