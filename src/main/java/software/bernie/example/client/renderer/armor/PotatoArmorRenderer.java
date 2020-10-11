package software.bernie.example.client.renderer.armor;

import software.bernie.example.client.renderer.model.armor.PotatoArmorModel;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.geckolib.renderers.geo.GeoArmorRenderer;

public class PotatoArmorRenderer extends GeoArmorRenderer<PotatoArmorItem>
{
	public PotatoArmorRenderer()
	{
		super(new PotatoArmorModel());
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
