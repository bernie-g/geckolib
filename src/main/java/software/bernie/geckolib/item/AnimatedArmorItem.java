package software.bernie.geckolib.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.model.AnimatedArmorModel;

public abstract class AnimatedArmorItem extends ArmorItem
{
	static
	{
		AnimationController.addModelFetcher((Object object) ->
		{
			if (object instanceof AnimatedArmorItem)
			{
				AnimatedArmorItem armorItem = (AnimatedArmorItem) object;
				return armorItem.getModel();
			}
			return null;
		});
	}

	public AnimatedArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder)
	{
		super(materialIn, slot, builder);
	}

	public abstract AnimatedArmorModel getModel();
}
