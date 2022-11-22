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
public class MutantZombieModel extends DefaultedEntityGeoModel<MutantZombieEntity> {
	public MutantZombieModel() {
		super(new ResourceLocation(GeckoLib.ModID, "mutant_zombie"));
	}
}