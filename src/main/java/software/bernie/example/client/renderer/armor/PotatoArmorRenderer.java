package software.bernie.example.client.renderer.armor;

import software.bernie.example.client.model.armor.PotatoArmorModel;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

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
