package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.renderer.entity.MutantZombieRenderer;
import software.bernie.example.entity.DynamicExampleEntity;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link DynamicExampleEntity}
 * @see MutantZombieRenderer
 */
public class MutantZombieModel extends DefaultedEntityGeoModel<DynamicExampleEntity> {
	public MutantZombieModel() {
		super(new ResourceLocation(GeckoLibConstants.MODID, "mutant_zombie"));
	}
}