package software.bernie.geckolib.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.event.predicate.EntityAnimationPredicate;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatable;

import javax.annotation.Nullable;

public class BrownEntity extends AnimalEntity implements IAnimatable
{
	AnimationManager collection = new AnimationManager();
	AnimationController controller = new EntityAnimationController(this, "controller", 30, this::predicate);


	int ticksExecuting = 0;

	private <ENTITY extends Entity & IAnimatable> boolean predicate(EntityAnimationPredicate<ENTITY> event)
	{

		controller.setAnimation(new AnimationBuilder().addAnimation("crawling", true));
		ticksExecuting = 0;

		return true;
	}

	public BrownEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		collection.addAnimationController(controller);
	}

	@Nullable
	@Override
	public AgeableEntity createChild(AgeableEntity ageable)
	{
		return null;
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return collection;
	}
}
