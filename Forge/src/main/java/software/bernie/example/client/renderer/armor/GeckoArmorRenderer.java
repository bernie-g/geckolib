package software.bernie.example.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.client.model.armor.GeckoArmorModel;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedItemGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

/**
 * Example {@link software.bernie.geckolib3.renderers.geo.GeoRenderer} for the {@link GeckoArmorItem} example item
 */
public final class GeckoArmorRenderer extends GeoArmorRenderer<GeckoArmorItem> {
	public GeckoArmorRenderer() {
		super(new DefaultedItemGeoModel<>(new ResourceLocation(GeckoLib.MOD_ID, "armor/geckoarmor")));

		// Setting the various bone names to match what our bones are named in the geo.json
		// GeckoLib pre-populates this list with the default values from the Blockbench plugin
		// If your bone names are different you will need to set them like below
		// Failing to do this will cause the renderer to fail to render properly

		this.headBone = "helmet";
		this.bodyBone = "chestplate";
		this.rightArmBone = "rightArm";
		this.leftArmBone = "leftArm";
		this.rightLegBone = "rightLeg";
		this.leftLegBone = "leftLeg";
		this.rightBootBone = "rightBoot";
		this.leftBootBone = "leftBoot";
	}
}
