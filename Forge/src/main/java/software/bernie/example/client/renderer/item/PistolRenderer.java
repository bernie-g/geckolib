package software.bernie.example.client.renderer.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedItemGeoModel;
import software.bernie.geckolib3.renderer.GeoItemRenderer;

/**
 * Example {@link software.bernie.geckolib3.renderer.GeoItemRenderer} for {@link PistolItem}
 */
public class PistolRenderer extends GeoItemRenderer<PistolItem> {
	public PistolRenderer() {
		super(new DefaultedItemGeoModel<>(new ResourceLocation(GeckoLib.MOD_ID, "pistol")));
	}
}