package software.bernie.geckolib3q;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ArmorRenderingRegistryImpl {
	private ArmorRenderingRegistryImpl() {
	}

	public static HumanoidModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			HumanoidModel<LivingEntity> defaultModel) {
		return defaultModel;
	}

	public static ResourceLocation getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			boolean secondLayer, @Nullable String suffix, ResourceLocation defaultTexture) {
		return defaultTexture;
	}
}