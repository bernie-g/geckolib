package software.bernie.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.manager.AnimationManager;
import software.bernie.geckolib.core.IAnimatable;

public class LightCrystalEntity extends MobEntity implements IAnimatable
{
	public AnimationManager controllers = new AnimationManager();
	public AnimationController animationController = new AnimationController(this, "default", 0f, this::playAnimation);

	private <ENTITY extends Entity & IAnimatable> PlayState playAnimation(AnimationEvent<ENTITY> event)
	{
		animationController.setAnimation(new AnimationBuilder().addAnimation("animation.crystal2.new", true));
		return PlayState.CONTINUE;
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
