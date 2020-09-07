package software.bernie.geckolib.example.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.block.SpecialAnimationController;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.SpecialAnimationPredicate;
import software.bernie.geckolib.manager.AnimationManager;

public class GeoExampleEntity extends CreatureEntity implements IAnimatable
{
	AnimationManager manager = new AnimationManager();
	SpecialAnimationController controller = new SpecialAnimationController(this, "controller", 0, this::predicate);

	private <E extends IAnimatable> boolean predicate(SpecialAnimationPredicate<E> eSpecialAnimationPredicate)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", true).addAnimation("Botarium.anim.idle", true));
		return true;
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
