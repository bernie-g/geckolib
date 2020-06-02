package software.bernie.geckolib.test.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.IAnimatedEntity;
import software.bernie.geckolib.animation.AnimationBuilder;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationControllerCollection;
import software.bernie.geckolib.animation.AnimationTestEvent;
import software.bernie.geckolib.test.KeyboardHandler;

public class TigrisEntity extends GhastEntity implements IAnimatedEntity
{
	public AnimationControllerCollection animationControllers = new AnimationControllerCollection();
	private AnimationController moveController = new AnimationController(this, "moveController", 10F, this::moveController);

	private <ENTITY extends Entity> boolean moveController(AnimationTestEvent<ENTITY> entityAnimationTestEvent)
	{
		moveController.transitionLength = 10;
		if(KeyboardHandler.isForwardKeyDown)
		{
			moveController.setAnimation(new AnimationBuilder().addAnimation("tigris.spitfly", false).addAnimation("tigris.sit", true));
		}
		else {
			moveController.setAnimation(new AnimationBuilder().addAnimation("tigris.fly"));
		}
		return true;
	}

	public TigrisEntity(EntityType<? extends GhastEntity> type, World worldIn)
	{
		super(type, worldIn);
		registerAnimationControllers();
	}

	@Override
	public AnimationControllerCollection getAnimationControllers()
	{
		return animationControllers;
	}

	public void registerAnimationControllers()
	{
		if(world.isRemote)
		{
			this.animationControllers.addAnimationController(moveController);
		}
	}
}
