package software.bernie.geckolib.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.EntityAnimationPredicate;
import software.bernie.geckolib.manager.AnimationManager;

import javax.annotation.Nullable;

public class RobotEntity extends AnimalEntity implements IAnimatable
{
	AnimationManager manager = new AnimationManager();
	EntityAnimationController controller = new EntityAnimationController(this, "walkController", 20, this::animationPredicate);

	private <E extends Entity & IAnimatable> boolean animationPredicate(EntityAnimationPredicate<E> event)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("walk"));
		return true;
	}

	public RobotEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		manager.addAnimationController(controller);
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
		return manager;
	}
}
