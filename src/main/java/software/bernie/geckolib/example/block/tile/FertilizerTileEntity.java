package software.bernie.geckolib.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.block.SpecialAnimationController;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.SpecialAnimationPredicate;
import software.bernie.geckolib.example.registry.TileRegistry;
import software.bernie.geckolib.manager.AnimationManager;

public class FertilizerTileEntity extends TileEntity implements IAnimatable
{
	private final AnimationManager manager = new AnimationManager();
	private final SpecialAnimationController controller = new SpecialAnimationController(this, "controller", 0, this::predicate);

	private <E extends TileEntity & IAnimatable> boolean predicate(SpecialAnimationPredicate<E> eSpecialAnimationPredicate)
	{
		controller.transitionLengthTicks = 0;
		controller.setAnimation(new AnimationBuilder().addAnimation("fertilizer.animation.deploy", false).addAnimation("fertilizer.animation.idle", true));
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
