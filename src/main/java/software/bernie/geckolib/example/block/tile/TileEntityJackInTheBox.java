package software.bernie.geckolib.example.block.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.event.TileAnimationPredicate;
import software.bernie.geckolib.example.registry.TileRegistry;
import software.bernie.geckolib.tesr.BlockAnimationController;
import software.bernie.geckolib.tesr.BlockAnimationManager;
import software.bernie.geckolib.tesr.ITileAnimatable;

public class TileEntityJackInTheBox extends TileEntity implements ITileAnimatable, ITickableTileEntity
{
	private final BlockAnimationManager manager = new BlockAnimationManager();
	private final BlockAnimationController controller = new BlockAnimationController(this, "controller", 0, this::predicate);

	private <E extends TileEntity & ITileAnimatable> boolean predicate(TileAnimationPredicate<E> eTileAnimationPredicate)
	{
		controller.transitionLengthTicks = 20;
		controller.setAnimation(new AnimationBuilder().addAnimation("animation.model.ChestPopUpIdle", true));
		return true;
	}

	public TileEntityJackInTheBox()
	{
		super(TileRegistry.JACK_IN_THE_BOX_TILE.get());
		manager.addAnimationController(controller);
	}


	@Override
	public BlockAnimationManager getAnimationManager()
	{
		return manager;
	}

	@Override
	public void tick()
	{
		if(world.isRemote)
		{
			manager.ticksExisted++;
		}
	}
}
