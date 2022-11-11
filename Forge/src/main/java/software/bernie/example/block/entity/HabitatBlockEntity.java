package software.bernie.example.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.example.client.renderer.block.HabitatBlockRenderer;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.animatable.GeoBlock;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animation.AnimationData;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example {@link BlockEntity} implementation using a GeckoLib model.
 * @see software.bernie.example.client.model.block.HabitatModel
 * @see HabitatBlockRenderer
 */
public class HabitatBlockEntity extends BlockEntity implements GeoBlock {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public HabitatBlockEntity(BlockPos pos, BlockState state) {
		super(TileRegistry.HABITAT_TILE.get(), pos, state);
	}

	/**
	 * We just want a permanent idle animation happening here
	 */
	@Override
	public void registerControllers(AnimationData<?> data) {
		data.addAnimationController(DefaultAnimations.genericIdleController(this));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
