package software.bernie.geckolib.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.manager.EntityAnimationManager;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;

import javax.annotation.Nullable;

public class BrownEntity extends AnimalEntity implements IAnimatedEntity
{
	EntityAnimationManager collection = new EntityAnimationManager();
	AnimationController controller = new EntityAnimationController(this, "controller", 30, this::predicate);

	private <ENTITY extends Entity> boolean predicate(AnimationTestEvent<ENTITY> event)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("crawling", true));
		return true;
	}

	public BrownEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		collection.addAnimationController(controller);
	}


	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return collection;
	}

	@Nullable
	@Override
	public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
	{
		return null;
	}
}
