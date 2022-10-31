package software.bernie.example.block.tile;

import net.minecraft.block.entity.BlockEntity;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class FertilizerTileEntity extends BlockEntity implements IAnimatable {
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public FertilizerTileEntity() {
		super(TileRegistry.FERTILIZER);
	}

	private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().transitionLengthTicks = 0;
		if (event.getAnimatable().getWorld().isRaining()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("fertilizer.animation.deploy", EDefaultLoopTypes.LOOP)
					.addAnimation("fertilizer.animation.idle", EDefaultLoopTypes.LOOP));
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", EDefaultLoopTypes.LOOP)
					.addAnimation("Botarium.anim.idle", EDefaultLoopTypes.LOOP));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(
				new AnimationController<FertilizerTileEntity>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
