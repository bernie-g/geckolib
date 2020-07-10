package software.bernie.geckolib.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.AnimationBuilder;
import software.bernie.geckolib.animation.AnimationTestEvent;
import software.bernie.geckolib.animation.model.AnimationController;
import software.bernie.geckolib.animation.model.AnimationControllerCollection;
import software.bernie.geckolib.entity.IAnimatedEntity;

import javax.annotation.Nullable;

public class EasingDemoEntity extends AnimalEntity implements IAnimatedEntity
{
	AnimationControllerCollection collection = new AnimationControllerCollection();
	AnimationController easingDemoControlller = new AnimationController(this, "easingDemoController", 20, this::demoPredicate);

	private <ENTITY extends Entity> boolean demoPredicate(AnimationTestEvent<ENTITY> event)
	{
		easingDemoControlller.setAnimation(new AnimationBuilder().addAnimation("animation.easing1.new", true));
		return true;
	}

	public EasingDemoEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		collection.addAnimationController(easingDemoControlller);
	}

	@Nullable
	@Override
	public AgeableEntity createChild(AgeableEntity ageable)
	{
		return null;
	}

	@Override
	public AnimationControllerCollection getAnimationControllers()
	{
		return collection;
	}
}
