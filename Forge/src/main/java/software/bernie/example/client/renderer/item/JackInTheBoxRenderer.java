package software.bernie.example.client.renderer.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedItemGeoModel;
import software.bernie.geckolib3.renderer.GeoItemRenderer;

/**
 * Example {@link software.bernie.geckolib3.renderer.GeoItemRenderer} for {@link JackInTheBoxItem}
 */
public class JackInTheBoxRenderer extends GeoItemRenderer<JackInTheBoxItem> {
	public JackInTheBoxRenderer() {
		super(new DefaultedItemGeoModel<>(new ResourceLocation(GeckoLib.MOD_ID, "jack_in_the_box")));
	}
}
