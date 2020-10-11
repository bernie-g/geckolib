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
		this.bodyBone = "body";
		this.rightArmBone = "right_arm";
		this.leftArmBone = "left_arm";
		this.rightLegBone = "leg3";
		this.leftLegBone = "leg2";
		this.rightBootBone = "FootRight3";
		this.leftBootBone = "FootRight2";
	}
}
