package software.bernie.example.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedItemGeoModel;
import software.bernie.geckolib3.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link JackInTheBoxItem}
 * @see software.bernie.example.client.renderer.item.JackInTheBoxRenderer
 */
public class JackInTheBoxModel extends DefaultedItemGeoModel<JackInTheBoxItem> {
	public JackInTheBoxModel() {
		super(new ResourceLocation(GeckoLib.MOD_ID, "jack_in_the_box"));
	}
}