package software.bernie.geckolib.test.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
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
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.IAnimatedEntity;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationControllerCollection;
import software.bernie.geckolib.animation.AnimationTestEvent;

public class AscendedLegfishEntity extends MonsterEntity implements IAnimatedEntity
{
	private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(AscendedLegfishEntity.class, DataSerializers.VARINT);

	public AnimationControllerCollection animationControllers = new AnimationControllerCollection();

	private AnimationController sizeController = new AnimationController(this, "sizeController", 0.01F, this::sizeAnimationPredicate);
	private AnimationController moveController = new AnimationController(this, "moveController", 1.5F, this::moveController);

	private <ENTITY extends Entity> boolean moveController(AnimationTestEvent<ENTITY> entityAnimationTestEvent)
	{
		//GeckoLib.LOGGER.info(entityAnimationTestEvent.getLimbSwing());
		float limbSwingAmount = entityAnimationTestEvent.getLimbSwingAmount();
		if(!(limbSwingAmount > -0.3F && limbSwingAmount < 0.3F))
		{
			moveController.setAnimation("walk", true);
			return true;
		}
		return false;
	}


	private boolean hasGrown = false;
	private <ENTITY extends Entity> boolean sizeAnimationPredicate(AnimationTestEvent<ENTITY> entityAnimationTestEvent)
	{
		int size = getSize();
		switch(size)
		{
			case 1:
				sizeController.setAnimation("small");
				break;
			case 2 :
				if(!hasGrown)
				{
 					sizeController.addAnimationToQueue("grow", false).addAnimationToQueue("upbig", true);
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
		if(world.isRemote)
		{
			this.animationControllers.addAnimationController(sizeController);
			this.animationControllers.addAnimationController(moveController);
		}
	}

	@Override
	public AnimationControllerCollection getAnimationControllers()
	{
		return animationControllers;
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
		if(source.getTrueSource() instanceof PlayerEntity)
		{
			if(getSize() == 1)
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

	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
	}

}
