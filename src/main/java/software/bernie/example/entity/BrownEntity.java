package software.bernie.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.manager.AnimationManager;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;

import javax.annotation.Nullable;

public class BrownEntity extends AnimalEntity implements IAnimatable
{
	AnimationManager collection = new AnimationManager();
	AnimationController controller = new AnimationController(this, "controller", 30, this::predicate);


	int ticksExecuting = 0;

	private <ENTITY extends Entity & IAnimatable> boolean predicate(AnimationTestPredicate<ENTITY> event)
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
