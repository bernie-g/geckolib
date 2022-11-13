package software.bernie.example.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.example.client.renderer.block.FertilizerBlockRenderer;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.animatable.GeoBlockEntity;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.RawAnimation;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example {@link BlockEntity} implementation using a GeckoLib model.
 * @see software.bernie.example.client.model.block.FertilizerModel
 * @see FertilizerBlockRenderer
 */
public class FertilizerBlockEntity extends BlockEntity implements GeoBlockEntity {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	// We statically instantiate our RawAnimations for efficiency, consistency, and error-proofing
	private static final RawAnimation FERTILIZER_ANIMS = RawAnimation.begin().thenPlay("fertilizer.deploy").thenLoop("fertilizer.idle");
	private static final RawAnimation BOTARIUM_ANIMS = RawAnimation.begin().thenPlay("botarium.deploy").thenLoop("botarium.idle");

	public FertilizerBlockEntity(BlockPos pos, BlockState state) {
		super(TileRegistry.FERTILIZER.get(), pos, state);
	}

	/**
	 * Let's set our animations up.<br>
	 * For this one, we want it to play the "Fertilizer" animation set if it's raining,
	 * or switch to a botarium if it's not.
	 */
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		manager.addAnimationController(new AnimationController<>(this, event -> {
			if (event.getAnimatable().getLevel().isRaining()) {
				event.getController().setAnimation(FERTILIZER_ANIMS);
			}
			else {
				event.getController().setAnimation(BOTARIUM_ANIMS);
			}

			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
