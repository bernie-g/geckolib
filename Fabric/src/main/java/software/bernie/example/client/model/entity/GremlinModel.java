package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.renderer.entity.MutantZombieRenderer;
import software.bernie.example.entity.MutantZombieEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link MutantZombieEntity}
 * @see MutantZombieRenderer
 */
public class GremlinModel extends DefaultedEntityGeoModel<MutantZombieEntity> {
	public GremlinModel() {
		super(new ResourceLocation(GeckoLib.ModID, "cqr_gremlin"));
	}
}