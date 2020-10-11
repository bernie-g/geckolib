package software.bernie.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.controller.BaseAnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;

public class FertilizerTileEntity extends TileEntity implements IAnimatable
{
	private final AnimationFactory manager = new AnimationFactory(this);

	private <E extends TileEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		BaseAnimationController controller = event.getController();
		controller.transitionLengthTicks = 0;
		if (event.getAnimatable().getWorld().isRaining())
		{
			controller.setAnimation(new AnimationBuilder().addAnimation("fertilizer.animation.deploy", true).addAnimation("fertilizer.animation.idle", true));
		}
		else
		{
			controller.setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", true).addAnimation("Botarium.anim.idle", true));
		}
		return PlayState.CONTINUE;
	}

	public FertilizerTileEntity()
	{
		super(TileRegistry.FERTILIZER.get());
	}


	@Override
	public void registerControllers(AnimationData data)
	{
		data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory()
	{
		return this.manager;
	}
}
