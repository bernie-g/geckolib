package software.bernie.geckolib.example.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.EntityAnimationPredicate;
import software.bernie.geckolib.manager.AnimationManager;

public class GeoExampleEntity extends CreatureEntity implements IAnimatable
{
	AnimationManager manager = new AnimationManager();
	EntityAnimationController controller = new EntityAnimationController(this, "controller", 20, this::predicate);

	public GeoExampleEntity(EntityType<? extends CreatureEntity> type, World worldIn)
	{
		super(type, worldIn);
		manager.addAnimationController(controller);
	}

	private <E extends Entity & IAnimatable> boolean predicate(EntityAnimationPredicate<E> eEntityAnimationPredicate)
	{
 		controller.setAnimation(new AnimationBuilder().addAnimation("somenewanimationidk", true));
		return true;
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}
}
