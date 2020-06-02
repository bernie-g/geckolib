/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.animation.model.AnimationControllerCollection;

public class TurretEntity extends MobEntity implements IAnimatedEntity
{
	protected TurretEntity(EntityType<? extends MobEntity> type, World worldIn)
	{
		super(type, worldIn);
	}


	@Override
	public AnimationControllerCollection getAnimationControllers()
	{
		return new AnimationControllerCollection();
	}
}
