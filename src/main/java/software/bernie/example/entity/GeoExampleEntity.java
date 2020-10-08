package software.bernie.example.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationManager;

public class GeoExampleEntity extends CreatureEntity implements IAnimatable
{
	AnimationManager manager = new AnimationManager();
	AnimationController controller = new AnimationController(this, "controller", 0, this::predicate);

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> eSpecialAnimationPredicate)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", true).addAnimation("Botarium.anim.idle", true));
		return PlayState.CONTINUE;
	}

	public GeoExampleEntity(EntityType<? extends CreatureEntity> type, World worldIn)
	{
		super(type, worldIn);
		this.ignoreFrustumCheck = true;
		manager.addAnimationController(controller);
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}
}
