package software.bernie.geckolib3q;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ArmorProvider {
	HumanoidModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			HumanoidModel<LivingEntity> defaultModel);

	ResourceLocation getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			ResourceLocation defaultTexture);
}