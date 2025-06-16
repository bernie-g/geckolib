package anightdazingzoroark.example.client.renderer.armor;

import anightdazingzoroark.example.client.model.armor.PotatoArmorModel;
import anightdazingzoroark.example.item.PotatoArmorItem;
import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;

public class PotatoArmorRenderer extends GeoArmorRenderer<PotatoArmorItem> {
	public PotatoArmorRenderer() {
		super(new PotatoArmorModel());

		// These values are what each bone name is in blockbench. So if your head bone
		// is named "bone545", make sure to do this.headBone = "bone545";
		// The default values are the ones that come with the default armor template in
		// the geckolib blockbench plugin.
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
