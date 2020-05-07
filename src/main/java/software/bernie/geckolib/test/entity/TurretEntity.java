package software.bernie.geckolib.test.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.IAnimatedEntity;
import software.bernie.geckolib.animation.AnimationControllerCollection;

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
