package software.bernie.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationManager;

public class FertilizerTileEntity extends TileEntity implements IAnimatable
{
	private final AnimationManager manager = new AnimationManager();
	private final AnimationController controller = new AnimationController(this, "controller", 0, this::predicate);

	private <E extends TileEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
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
		manager.addAnimationController(controller);
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}


}
