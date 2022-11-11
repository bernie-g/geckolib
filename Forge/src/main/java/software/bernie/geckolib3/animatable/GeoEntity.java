package software.bernie.geckolib3.animatable;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.model.GeoModel;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable} interface specific to {@link net.minecraft.world.entity.Entity Entities}.
 * This also applies to Projectiles and other Entity subclasses.<br>
 * <b>NOTE:</b> This <u>cannot</u> be used for entities using the {@link software.bernie.geckolib3.renderer.GeoReplacedEntityRenderer}
 * as you aren't extending {@code Entity}. Use {@link GeoReplacedEntity} instead.
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Entity-Animations">GeckoLib Wiki - Entity Animations</a>
 */
public interface GeoEntity extends GeoAnimatable {
	@Override
	default Supplier<GeoModel<?>> getGeoModel() {
		return () -> RenderUtils.getGeoModelForEntity((Entity)this);
	}
}
