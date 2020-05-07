package software.bernie.geckolib.test.entity;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.IAnimatedEntity;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationControllerCollection;
import software.bernie.geckolib.animation.AnimationTestEvent;

public class StingrayTestEntity extends WaterMobEntity implements IAnimatedEntity
{
	public AnimationControllerCollection animationControllers = new AnimationControllerCollection();
	private AnimationController wingController = new AnimationController(this, "wingController", 1, this::wingAnimationPredicate);

	@Override
	public AnimationControllerCollection getAnimationControllers()
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
			wingController.setAnimation("swimmingAnimation");
			this.animationControllers.addAnimationController(wingController);
		}
	}

	public boolean wingAnimationPredicate(AnimationTestEvent event)
	{
		Entity entity = event.getEntity();
		ClientWorld entityWorld = (ClientWorld) entity.getEntityWorld();
		if(entityWorld.rainingStrength > 0)
		{
			wingController.transitionLength = 2;
			wingController.setAnimation("thirdAnimation");
		}
		else {
			wingController.transitionLength = 2;
			wingController.setAnimation("secondAnimation");
		}
		return true;
	}


}
