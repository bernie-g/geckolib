package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

public class BrownEntity extends EntityAnimal implements IAnimatedEntity
{
	EntityAnimationManager manager = new EntityAnimationManager();
	AnimationController controller = new EntityAnimationController(this, "controller", 30, this::predicate);

	private <ENTITY extends Entity> boolean predicate(AnimationTestEvent<ENTITY> event)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("crawling", true));
		return true;
	}

	public BrownEntity(World worldIn)
	{
		super(worldIn);
		registerAnimationControllers();
	}

	public void registerAnimationControllers()
	{
		if (world.isRemote)
		{
			controller.setAnimation(new AnimationBuilder().addAnimation("running"));
			this.manager.addAnimationController(controller);
		}
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
