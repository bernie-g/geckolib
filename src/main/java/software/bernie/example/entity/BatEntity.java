package software.bernie.example.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationManager;

public class BatEntity extends CreatureEntity implements IAnimatable
{
	AnimationManager manager = new AnimationManager();
	AnimationController controller = new AnimationController(this, "controller", 20, this::predicate);

	public BatEntity(EntityType<? extends CreatureEntity> type, World worldIn)
	{
		super(type, worldIn);
		manager.addAnimationController(controller);
	}

	private <E extends Entity & IAnimatable> PlayState predicate(AnimationEvent<E> eAnimationEvent)
	{
 		controller.setAnimation(new AnimationBuilder().addAnimation("animation.bat.fly", true));
		return PlayState.CONTINUE;
	}


	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}
}
