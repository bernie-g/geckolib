package software.bernie.geckolib.test.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import software.bernie.geckolib.IAnimatedEntity;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationControllerCollection;
import software.bernie.geckolib.animation.AnimationTestEvent;

public class AscendedLegfishEntity extends MonsterEntity implements IAnimatedEntity
{
	private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(AscendedLegfishEntity.class, DataSerializers.VARINT);

	public AnimationControllerCollection animationControllers = new AnimationControllerCollection();
	private AnimationController sizeController = new AnimationController(this, "sizeController", 0.5F, this::sizeAnimationPredicate);

	private <ENTITY extends Entity> boolean sizeAnimationPredicate(AnimationTestEvent<ENTITY> entityAnimationTestEvent)
	{
		int size = getSize();
		switch(size)
		{
			case 1:
				//sizeController.setAnimation("small");
				break;
			case 2:
				sizeController.addAnimationToQueue("grow", false);//.addAnimationToQueue("upbig", true);
				setSize(3);
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
			sizeController.setAnimation("swimmingAnimation");
			this.animationControllers.addAnimationController(sizeController);
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
		if(getSize() == 1)
		{
			setSize(2);
		}
		return super.attackEntityFrom(source, amount);
	}
}
