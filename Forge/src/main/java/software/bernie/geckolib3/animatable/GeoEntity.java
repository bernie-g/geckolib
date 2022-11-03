package software.bernie.geckolib3.animatable;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.model.GeoModelProvider;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable} interface specific to {@link net.minecraft.world.entity.Entity Entities}.
 * This also applies to Projectiles and other Entity subclasses.
 */
public interface GeoEntity extends GeoAnimatable {
	@Override
	default Supplier<GeoModelProvider> getGeoModelProvider() {
		return () -> RenderUtils.getGeoModelProviderForEntity((Entity)this);
	}
}
