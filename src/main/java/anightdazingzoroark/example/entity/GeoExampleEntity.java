package anightdazingzoroark.example.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.IAnimationTickable;
import anightdazingzoroark.riftlib.core.PlayState;
import anightdazingzoroark.riftlib.core.builder.AnimationBuilder;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.event.predicate.AnimationEvent;
import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.core.manager.AnimationFactory;

public class GeoExampleEntity extends EntityCreature implements IAnimatable, IAnimationTickable {
	private AnimationFactory factory = new AnimationFactory(this);

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bat.fly", true));
		return PlayState.CONTINUE;
	}

	public GeoExampleEntity(World worldIn) {
		super(worldIn);
		this.ignoreFrustumCheck = true;
		this.setSize(0.7F, 1.3F);
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<GeoExampleEntity>(this, "controller", 50, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		super.initEntityAI();
	}

	@Override
	public int tickTimer() {
		return ticksExisted;
	}

	@Override
	public void tick() {
		super.onUpdate();
	}
}
