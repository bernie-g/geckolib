package software.bernie.example.client.renderer.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Example {@link GeoItemRenderer} for {@link PistolItem}
 */
public class PistolRenderer extends GeoItemRenderer<PistolItem> {
	public PistolRenderer() {
		super(new DefaultedItemGeoModel<>(new ResourceLocation(GeckoLib.MOD_ID, "pistol")));
	}
}