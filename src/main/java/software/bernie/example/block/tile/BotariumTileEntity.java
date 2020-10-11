package software.bernie.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;

public class BotariumTileEntity extends TileEntity implements IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);

	private <E extends TileEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		event.getController().transitionLengthTicks = 0;
		event.getController().setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", true));
		return PlayState.CONTINUE;
	}

	public BotariumTileEntity()
	{
		super(TileRegistry.BOTARIUM_TILE.get());
	}

	@Override
	public void registerControllers(AnimationData data)
	{
		data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
}
