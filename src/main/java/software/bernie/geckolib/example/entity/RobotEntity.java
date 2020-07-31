package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

public class RobotEntity extends EntityAnimal implements IAnimatedEntity
{
	EntityAnimationManager manager = new EntityAnimationManager();
	EntityAnimationController controller = new EntityAnimationController(this, "walkController", 20, this::animationPredicate);

	private <E extends Entity> boolean animationPredicate(AnimationTestEvent<E> event)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("walk"));
		return true;
	}

	public RobotEntity(World worldIn)
	{
		super(worldIn);
		manager.addAnimationController(controller);
	}


	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return manager;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable)
	{
		return null;
	}
}
