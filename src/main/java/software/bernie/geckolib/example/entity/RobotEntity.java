package software.bernie.geckolib.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

import javax.annotation.Nullable;

public class RobotEntity extends AnimalEntity implements IAnimatedEntity
{
	EntityAnimationManager manager = new EntityAnimationManager();
	EntityAnimationController controller = new EntityAnimationController(this, "walkController", 20, this::animationPredicate);

	private <E extends Entity> boolean animationPredicate(AnimationTestEvent<E> event)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("walk"));
		return true;
	}

	public RobotEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		manager.addAnimationController(controller);
	}


	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return manager;
	}


	@Nullable
	@Override
	public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
	{
		return null;
	}
}
