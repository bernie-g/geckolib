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
import software.bernie.geckolib.animation.model.AnimationController;
import software.bernie.geckolib.animation.model.AnimationControllerCollection;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.animation.*;

public class StingrayTestEntity extends WaterCreatureEntity implements IAnimatedEntity
{
	public AnimationControllerCollection animationControllers = new AnimationControllerCollection();
	private AnimationController wingController = new AnimationController(this, "wingController", 1, this::wingAnimationPredicate);

	@Override
	public AnimationControllerCollection getAnimationControllers()
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

	public boolean wingAnimationPredicate(AnimationTestEvent event)
	{
		Entity entity = event.getEntity();
		ClientWorld entityWorld = (ClientWorld) entity.getEntityWorld();
		if(entityWorld.rainGradient > 0)
		{
			wingController.transitionLength = 40;
			wingController.setAnimation(new AnimationBuilder().addAnimation("thirdAnimation"));
		}
		else {
			wingController.transitionLength = 40;
			wingController.setAnimation(new AnimationBuilder().addAnimation("secondAnimation"));
		}
		return true;
	}


}
