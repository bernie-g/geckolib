package software.bernie.example.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.WolfArmorItem;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/**
 * Example {@link GeoArmorRenderer} for the {@link WolfArmorItem} example item
 */
public final class WolfArmorRenderer extends GeoArmorRenderer<WolfArmorItem> {
	public WolfArmorRenderer() {
		super(new DefaultedItemGeoModel<>(new ResourceLocation(GeckoLibConstants.MODID, "armor/wolf_armor")));
	}
}
