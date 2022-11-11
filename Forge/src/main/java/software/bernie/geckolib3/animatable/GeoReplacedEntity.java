package software.bernie.geckolib3.animatable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.model.GeoModel;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.function.Supplier;

/**
 * The {@link GeoAnimatable} interface specific to {@link Entity Entities}.
 * This interface is <u>specifically</u> for entities replacing the rendering of other, existing entities.
 * @see software.bernie.example.entity.ReplacedCreeperEntity
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Entity-Animations">GeckoLib Wiki - Entity Animations</a>
 */
public interface GeoReplacedEntity extends GeoAnimatable {
	@Override
	default Supplier<GeoModel<?>> getGeoModel() {
		return () -> RenderUtils.getGeoModelForEntityType(getReplacingEntityType());
	}

	/**
	 * Returns the {@link EntityType} this entity is intending to replace.<br>
	 * This is used for rendering an animation purposes.
	 */
	EntityType<?> getReplacingEntityType();
}
