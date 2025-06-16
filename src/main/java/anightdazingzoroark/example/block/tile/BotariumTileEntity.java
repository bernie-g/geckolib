package anightdazingzoroark.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.PlayState;
import anightdazingzoroark.riftlib.core.builder.AnimationBuilder;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.event.predicate.AnimationEvent;
import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.core.manager.AnimationFactory;

public class BotariumTileEntity extends TileEntity implements IAnimatable {
	private final AnimationFactory factory = new AnimationFactory(this);

	@SuppressWarnings("unchecked")
	private <E extends TileEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().transitionLengthTicks = 0;
		event.getController().setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", true));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(
				new AnimationController<BotariumTileEntity>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
}
