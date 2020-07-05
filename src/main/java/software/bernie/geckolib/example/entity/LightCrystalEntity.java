package software.bernie.geckolib.example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.AnimationBuilder;
import software.bernie.geckolib.animation.AnimationTestEvent;
import software.bernie.geckolib.animation.model.AnimationController;
import software.bernie.geckolib.animation.model.AnimationControllerCollection;
import software.bernie.geckolib.entity.IAnimatedEntity;

public class LightCrystalEntity extends MobEntity implements IAnimatedEntity
{
	public AnimationControllerCollection controllers = new AnimationControllerCollection();
	public AnimationController animationController = new AnimationController(this, "default", 0f, this::playAnimation);

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
