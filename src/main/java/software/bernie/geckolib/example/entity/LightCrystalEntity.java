package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.controller.AnimationControllerCollection;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;

public class LightCrystalEntity extends MobEntity implements IAnimatedEntity
{
	public AnimationControllerCollection controllers = new AnimationControllerCollection();
	public AnimationController animationController = new EntityAnimationController(this, "default", 0f, this::playAnimation);

	private <ENTITY extends Entity> boolean playAnimation(AnimationTestEvent<ENTITY> event)
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
	public AnimationControllerCollection getAnimationControllers()
	{
		return controllers;
	}
}
