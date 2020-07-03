/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.animation.AnimationBuilder;
import software.bernie.geckolib.animation.model.AnimationController;
import software.bernie.geckolib.animation.model.AnimationControllerCollection;
import software.bernie.geckolib.animation.AnimationTestEvent;
import software.bernie.geckolib.example.KeyboardHandler;

public class AscendedLegfishEntity extends HostileEntity implements IAnimatedEntity
{
	private static final TrackedData<Integer> SIZE = DataTracker.registerData(AscendedLegfishEntity.class, TrackedDataHandlerRegistry.INTEGER);

	public AnimationControllerCollection animationControllers = new AnimationControllerCollection();

	private AnimationController sizeController = new AnimationController(this, "sizeController", 1F, this::sizeAnimationPredicate);
	private AnimationController moveControl = new AnimationController(this, "moveController", 10F, this::moveController);

	private <ENTITY extends Entity> boolean moveController(AnimationTestEvent<ENTITY> entityAnimationTestEvent)
	{
		float limbSwingAmount = entityAnimationTestEvent.getLimbSwingAmount();
		if(KeyboardHandler.isForwardKeyDown)
		{
			moveControl.setAnimation(new AnimationBuilder().addAnimation("kick", true));
			return true;
		}
		else if(KeyboardHandler.isBackKeyDown)
		{
			moveControl.setAnimation(new AnimationBuilder().addAnimation("punchwalk", true));
			return true;
		}
		else if(!(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F))
		{
			moveControl.setAnimation(new AnimationBuilder().addAnimation("walk", true));
			return true;
		}
		return false;
	}


	private boolean hasGrown = false;
	private <ENTITY extends Entity> boolean sizeAnimationPredicate(AnimationTestEvent<ENTITY> entityAnimationTestEvent)
	{
		int size = getDimensions();
		switch(size)
		{
			case 1:
				sizeController.setAnimation(new AnimationBuilder().addAnimation("small"));
				break;
			case 2 :
				if(!hasGrown)
				{
 					sizeController.setAnimation(new AnimationBuilder().addAnimation("grow", false).addAnimation("upbig", true));
					setSize(3);
					hasGrown = true;
				}
		}
		return true;
	}

	public AscendedLegfishEntity(EntityType<? extends HostileEntity> type, World worldIn)
	{
		super(type, worldIn);
		registerAnimationControllers();
	}

	public void registerAnimationControllers()
	{
		if(world.isClient)
		{
			this.animationControllers.addAnimationController(sizeController);
			this.animationControllers.addAnimationController(moveControl);
		}
	}

	@Override
	public AnimationControllerCollection getAnimationControllers()
	{
		return animationControllers;
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(SIZE, 1);
	}

	public int getDimensions()
	{
		return this.dataTracker.get(SIZE);
	}

	public void setSize(int size)
	{
		this.dataTracker.set(SIZE, size);
	}

	/**
	 * Called when the entity is attacked.
	 *
	 * @param source
	 * @param amount
	 */
	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if(source.getAttacker() instanceof PlayerEntity)
		{
			if(getDimensions() == 1)
			{
				setSize(2);
			}
		}
		return super.damage(source, amount);
	}

	@Override
	protected void initGoals()
	{
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}

	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0D);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
	}

}
