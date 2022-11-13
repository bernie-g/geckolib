package software.bernie.example.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedItemGeoModel;
import software.bernie.geckolib3.renderer.GeoArmorRenderer;
import software.bernie.geckolib3.renderer.GeoRenderer;

/**
 * Example {@link GeoRenderer} for the {@link GeckoArmorItem} example item
 */
public final class GeckoArmorRenderer extends GeoArmorRenderer<GeckoArmorItem> {
	public GeckoArmorRenderer() {
		super(new DefaultedItemGeoModel<>(new ResourceLocation(GeckoLib.MOD_ID, "armor/geckoarmor")));
	}
}
