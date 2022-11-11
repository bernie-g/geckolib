package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.ReplacedCreeperEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedEntityGeoModel;

/**
 * Example {@link software.bernie.geckolib3.model.GeoModel} for dynamically replacing an
 * existing entity's renderer with a GeckoLib model (in this case, {@link net.minecraft.world.entity.monster.Creeper}
 * @see software.bernie.geckolib3.renderer.GeoReplacedEntityRenderer
 * @see software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer
 */
public class ReplacedCreeperModel extends DefaultedEntityGeoModel<ReplacedCreeperEntity> {
	public ReplacedCreeperModel() {
		super(new ResourceLocation(GeckoLib.MOD_ID, "creeper"));
	}
}
