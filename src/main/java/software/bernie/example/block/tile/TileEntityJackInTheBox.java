package software.bernie.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.core.manager.AnimationManager;


public class TileEntityJackInTheBox extends TileEntity implements IAnimatable
{
	private final AnimationManager manager = new AnimationManager();
	private final AnimationController controller = new AnimationController(this, "controller", 0, this::predicate);

	private <E extends TileEntity & IAnimatable> PlayState predicate(AnimationEvent<E> eSpecialAnimationPredicate)
	{
		controller.transitionLengthTicks = 20;
		controller.setAnimation(new AnimationBuilder().addAnimation("animation.model.ChestPopUp", false).addAnimation("animation.model.ChestPopUpIdle", false).addAnimation("animation.model.ChestClose").addAnimation("animation.model.ChestPeek"));
		return PlayState.CONTINUE;
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
