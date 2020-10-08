package software.bernie.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationManager;

import javax.annotation.Nullable;

public class EntityBotarium extends AnimalEntity implements IAnimatable
{
	AnimationManager manager = new AnimationManager();
	AnimationController controller = new AnimationController(this, "walkController", 0.1f, this::animationPredicate);

	private <E extends Entity & IAnimatable> PlayState animationPredicate(AnimationEvent<E> event)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", true).addAnimation("Botarium.anim.idle", true));
		return PlayState.CONTINUE;
	}

	public EntityBotarium(EntityType<? extends AnimalEntity> type, World worldIn)
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
