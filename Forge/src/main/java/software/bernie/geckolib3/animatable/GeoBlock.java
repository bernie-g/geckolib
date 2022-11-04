package software.bernie.geckolib3.animatable;

import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.model.provider.GeoModel;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable} interface specific to {@link net.minecraft.world.level.block.Block Blocks}.
 * Specifically, Geckolib currently only supports animations for {@link net.minecraft.world.level.block.entity.BlockEntity BlockEntities}
 */
public interface GeoBlock extends GeoAnimatable {
	@Override
	default Supplier<GeoModel<?>> getGeoModel() {
		return () -> RenderUtils.getGeoModelForBlock((BlockEntity)this);
	}
}
