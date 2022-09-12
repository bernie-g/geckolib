package software.bernie.geckolib3;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class ArmorRenderingRegistryImpl {
	private ArmorRenderingRegistryImpl() {
	}

	public static BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			BipedEntityModel<LivingEntity> defaultModel) {
		return defaultModel;
	}

	public static Identifier getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			boolean secondLayer, @Nullable String suffix, Identifier defaultTexture) {
		return defaultTexture;
	}
}