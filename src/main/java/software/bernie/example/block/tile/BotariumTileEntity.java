package software.bernie.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.IAnimatable;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.animation.manager.AnimationManager;

public class BotariumTileEntity extends TileEntity implements IAnimatable
{
	private final AnimationManager manager = new AnimationManager();
	private final AnimationController controller = new AnimationController(this, "controller", 0, this::predicate);

	private <E extends TileEntity & IAnimatable> boolean predicate(AnimationTestPredicate<E> eSpecialAnimationPredicate)
	{
		controller.transitionLengthTicks = 0;
		controller.setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", true).addAnimation("Botarium.anim.idle", true));
		return true;
	}

	public BotariumTileEntity()
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
