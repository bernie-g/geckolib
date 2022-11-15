package software.bernie.example.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.WolfArmorItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedItemGeoModel;
import software.bernie.geckolib3.renderer.GeoArmorRenderer;
import software.bernie.geckolib3.renderer.GeoRenderer;

/**
 * Example {@link GeoRenderer} for the {@link WolfArmorItem} example item
 */
public final class WolfArmorRenderer extends GeoArmorRenderer<WolfArmorItem> {
	public WolfArmorRenderer() {
		super(new DefaultedItemGeoModel<>(new ResourceLocation(GeckoLib.MOD_ID, "armor/wolf_armor")));
	}
}
