package software.bernie.geckolib.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.SpecialAnimationPredicate;
import software.bernie.geckolib.example.registry.TileRegistry;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.block.SpecialAnimationController;


public class TileEntityJackInTheBox extends TileEntity implements IAnimatable
{
	private final AnimationManager manager = new AnimationManager();
	private final SpecialAnimationController controller = new SpecialAnimationController(this, "controller", 0, this::predicate);

	private <E extends TileEntity & IAnimatable> boolean predicate(SpecialAnimationPredicate<E> eSpecialAnimationPredicate)
	{
		controller.transitionLengthTicks = 20;
		controller.setAnimation(new AnimationBuilder().addAnimation("animation.model.ChestPopUp", false).addAnimation("animation.model.ChestPopUpIdle", false).addAnimation("animation.model.ChestClose").addAnimation("animation.model.ChestPeek"));
		return true;
	}

	public TileEntityJackInTheBox()
	{
		super(TileRegistry.BOTARIUM_TILE.get());
		manager.addAnimationController(controller);
	}


	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}
}
