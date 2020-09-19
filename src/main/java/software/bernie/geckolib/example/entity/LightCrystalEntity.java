package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatable;

public class LightCrystalEntity extends MobEntity implements IAnimatable
{
	public AnimationManager controllers = new AnimationManager();
	public AnimationController animationController = new EntityAnimationController(this, "default", 0f, this::playAnimation);

	private <ENTITY extends Entity & IAnimatable> boolean playAnimation(AnimationTestPredicate<ENTITY> event)
	{
		animationController.setAnimation(new AnimationBuilder().addAnimation("animation.crystal2.new", true));
		return true;
	}

	public LightCrystalEntity(EntityType<? extends MobEntity> type, World worldIn)
	{
		super(type, worldIn);
		controllers.addAnimationController(animationController);
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return controllers;
	}
}
