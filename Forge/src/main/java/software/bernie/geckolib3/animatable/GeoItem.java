package software.bernie.geckolib3.animatable;

import net.minecraft.world.item.Item;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.model.GeoModelProvider;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable} interface specific to {@link net.minecraft.world.item.Item Items}.
 * This also applies to armor, as they are also just items
 */
public interface GeoItem extends GeoAnimatable {
	@Override
	default Supplier<GeoModelProvider> getGeoModelProvider() {
		return () -> RenderUtils.getGeoModelProviderForItem((Item)this);
	}
}
