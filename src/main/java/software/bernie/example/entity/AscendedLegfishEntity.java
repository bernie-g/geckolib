/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationTestPredicate;
import software.bernie.example.KeyboardHandler;
import software.bernie.geckolib.core.manager.AnimationManager;

public class AscendedLegfishEntity extends MonsterEntity implements IAnimatable
{
	private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(AscendedLegfishEntity.class, DataSerializers.VARINT);

	public AnimationManager animationManager = new AnimationManager();

	private AnimationController sizeController = new AnimationController(this, "sizeController", 1F, this::sizeAnimationPredicate);
	private AnimationController moveController = new AnimationController(this, "moveController", 10F, this::moveController);

	private <ENTITY extends Entity & IAnimatable> boolean moveController(AnimationTestPredicate<ENTITY> event)
	{
		float limbSwingAmount = event.getLimbSwingAmount();
		if (KeyboardHandler.isForwardKeyDown)
		{
			moveController.setAnimation(new AnimationBuilder().addAnimation("kick", true));
			return true;
		}
		else if (KeyboardHandler.isBackKeyDown)
		{
			moveController.setAnimation(new AnimationBuilder().addAnimation("punchwalk", true));
			return true;
		}
		else if (!(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F))
		{
			moveController.setAnimation(new AnimationBuilder().addAnimation("walk", true));
			return true;
		}
		return false;
	}


	private boolean hasGrown = false;

	private <ENTITY extends Entity & IAnimatable> boolean sizeAnimationPredicate(AnimationTestPredicate<ENTITY> entityAnimationTestPredicate)
	{
		int size = getSize();
		switch (size)
		{
			case 1:
				sizeController.setAnimation(new AnimationBuilder().addAnimation("small"));
				break;
			case 2:
				if (!hasGrown)
				{
					sizeController.setAnimation(new AnimationBuilder().addAnimation("grow", false).addAnimation("upbig", true));
					setSize(3);
					hasGrown = true;
				}
		}
		return true;
	}

	public AscendedLegfishEntity(EntityType<? extends MonsterEntity> type, World worldIn)
	{
		super(type, worldIn);
		registerAnimationControllers();
	}

	public void registerAnimationControllers()
	{
		if (world.isRemote)
		{
			this.animationManager.addAnimationController(sizeController);
			this.animationManager.addAnimationController(moveController);
		}
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return animationManager;
	}

	@Override
	protected void registerData()
	{
		super.registerData();
		this.dataManager.register(SIZE, 1);
	}

	public int getSize()
	{
		return this.dataManager.get(SIZE);
	}

	public void setSize(int size)
	{
		this.dataManager.set(SIZE, size);
	}

	/**
	 * Called when the entity is attacked.
	 *
	 * @param source
	 * @param amount
	 */
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (source.getTrueSource() instanceof PlayerEntity)
		{
			if (getSize() == 1)
			{
				setSize(2);
			}
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	}

	protected void registerAttributes()
	{
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double) 0.2F);
	}

}
