package software.bernie.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationTestPredicate;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.core.manager.AnimationManager;

public class FertilizerTileEntity extends TileEntity implements IAnimatable
{
	private final AnimationManager manager = new AnimationManager();
	private final AnimationController controller = new AnimationController(this, "controller", 0, this::predicate);

	private <E extends TileEntity & IAnimatable> boolean predicate(AnimationTestPredicate<E> eSpecialAnimationPredicate)
	{
		controller.transitionLengthTicks = 0;
		controller.setAnimation(new AnimationBuilder().addAnimation("fertilizer.animation.deploy", true).addAnimation("fertilizer.animation.idle", true));
		return true;
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
