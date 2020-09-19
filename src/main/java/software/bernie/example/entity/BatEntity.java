package software.bernie.example.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.IAnimatable;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.animation.manager.AnimationManager;

public class BatEntity extends CreatureEntity implements IAnimatable
{
	AnimationManager manager = new AnimationManager();
	AnimationController controller = new AnimationController(this, "controller", 20, this::predicate);

	public BatEntity(EntityType<? extends CreatureEntity> type, World worldIn)
	{
		super(type, worldIn);
		manager.addAnimationController(controller);
	}

	private <E extends Entity & IAnimatable> boolean predicate(AnimationTestPredicate<E> eAnimationTestPredicate)
	{
 		controller.setAnimation(new AnimationBuilder().addAnimation("animation.bat.fly", true));
		return true;
	}


	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}
}
