package software.bernie.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.manager.AnimationManager;
import software.bernie.geckolib.core.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib.core.event.ParticleKeyFrameEvent;

import javax.annotation.Nullable;
import java.util.Arrays;

public class EasingDemoEntity extends AnimalEntity implements IAnimatable
{
	AnimationManager collection = new AnimationManager();
	AnimationController easingDemoControlller = new AnimationController(this, "easingDemoController", 20, this::demoPredicate);

	private <ENTITY extends Entity & IAnimatable> boolean demoPredicate(AnimationTestPredicate<ENTITY> event)
	{
		easingDemoControlller.setAnimation(new AnimationBuilder().addAnimation("animation.model.new", true));
		return true;
	}

	public EasingDemoEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		collection.addAnimationController(easingDemoControlller);
		easingDemoControlller.registerParticleListener(this::listenForParticles);
		easingDemoControlller.registerCustomInstructionListener(this::customInstructionListener);
	}

	private <ENTITY extends IAnimatable> void customInstructionListener(CustomInstructionKeyframeEvent<ENTITY> event)
	{
		GeckoLib.LOGGER.info(Arrays.toString(event.instructions.toArray()));
	}

	private <ENTITY extends IAnimatable> void listenForParticles(ParticleKeyFrameEvent<ENTITY> event)
	{
		GeckoLib.LOGGER.info("{} {} {}", event.effect, event.locator, event);
	}

	@Nullable
	@Override
	public AgeableEntity createChild(AgeableEntity ageable)
	{
		return null;
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return collection;
	}
}
