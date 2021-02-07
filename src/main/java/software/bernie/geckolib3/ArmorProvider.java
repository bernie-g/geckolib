package software.bernie.geckolib3;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface ArmorProvider {
	BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			BipedEntityModel<LivingEntity> defaultModel);

	Identifier getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, Identifier defaultTexture);
}