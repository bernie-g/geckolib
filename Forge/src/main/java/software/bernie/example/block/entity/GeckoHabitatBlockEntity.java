package software.bernie.example.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.example.client.model.block.GeckoHabitatModel;
import software.bernie.example.client.renderer.block.GeckoHabitatBlockRenderer;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.animatable.GeoBlockEntity;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example {@link BlockEntity} implementation using a GeckoLib model.
 * @see GeckoHabitatModel
 * @see GeckoHabitatBlockRenderer
 */
public class GeckoHabitatBlockEntity extends BlockEntity implements GeoBlockEntity {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public GeckoHabitatBlockEntity(BlockPos pos, BlockState state) {
		super(TileRegistry.HABITAT_TILE.get(), pos, state);
	}

	// We just want a permanent idle animation happening here
	// But if it's day time we want him to take a nap
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		manager.addController(new AnimationController<>(this, event -> {
			if (getLevel().getDayTime() > 23000 || getLevel().getDayTime() < 13000) {
				event.getController().setAnimation(DefaultAnimations.REST);
			}
			else {
				event.getController().setAnimation(DefaultAnimations.IDLE);
			}

			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
