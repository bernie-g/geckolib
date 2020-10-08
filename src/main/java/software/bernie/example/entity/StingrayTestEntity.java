/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example.entity;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.manager.AnimationManager;
import software.bernie.geckolib.core.easing.EasingType;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;

public class StingrayTestEntity extends WaterMobEntity implements IAnimatable
{
	public AnimationManager animationControllers = new AnimationManager();
	private AnimationController wingController = new AnimationController(this, "wingController", 1, this::wingAnimationPredicate);

	@Override
	public AnimationManager getAnimationManager()
	{
		return animationControllers;
	}

	public StingrayTestEntity(EntityType<? extends WaterMobEntity> p_i48565_1_, World p_i48565_2_)
	{
		super(p_i48565_1_, p_i48565_2_);
		this.registerAnimationControllers();

	}

	public void registerAnimationControllers()
	{
		if(world.isRemote)
		{
			wingController.setAnimation(new AnimationBuilder().addAnimation("swimmingAnimation"));
			this.animationControllers.addAnimationController(wingController);
		}
	}

	<E extends IAnimatable> PlayState wingAnimationPredicate(AnimationEvent<E> event)
	{
		Entity entity = (Entity) event.getAnimatable();
		ClientWorld entityWorld = (ClientWorld) entity.getEntityWorld();
		wingController.easingType = EasingType.EaseInOutQuart;
		if(entityWorld.rainingStrength > 0)
		{
			wingController.transitionLengthTicks = 40;
			wingController.setAnimation(new AnimationBuilder().addAnimation("thirdAnimation"));
		}
		else {
			wingController.transitionLengthTicks = 40;
			wingController.setAnimation(new AnimationBuilder().addAnimation("secondAnimation"));
		}
		return PlayState.CONTINUE;
	}


}
