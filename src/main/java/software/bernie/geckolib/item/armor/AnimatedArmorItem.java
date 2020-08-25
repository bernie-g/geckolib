package software.bernie.geckolib.item.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import software.bernie.geckolib.animation.model.AnimatedArmorModel;

public abstract class AnimatedArmorItem extends ArmorItem
{
	public AnimatedArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder)
	{
		super(materialIn, slot, builder);
	}

	public abstract AnimatedArmorModel getModel();
}
