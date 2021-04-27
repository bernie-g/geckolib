package software.bernie.geckolib3;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class ArmorRenderingRegistryImpl {
	private ArmorRenderingRegistryImpl() {
	}

	public static BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			BipedEntityModel<LivingEntity> defaultModel) {
//		if (!stack.isEmpty()) {
//			ArmorRenderingRegistry.ModelProvider provider = ((ArmorProviderExtensions) stack.getItem())
//					.fabric_getArmorModelProvider();
//			if (provider != null) {
//				return provider.getArmorModel(entity, stack, slot, defaultModel);
//			}
//			if (stack.getItem() instanceof ArmorProvider) {
//				return ((ArmorProvider) stack.getItem()).getArmorModel(entity, stack, slot, defaultModel);
//			}
//		}
//
		return defaultModel;
	}

	public static Identifier getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
			boolean secondLayer, @Nullable String suffix, Identifier defaultTexture) {
//		if (!stack.isEmpty()) {
//			ArmorRenderingRegistry.TextureProvider provider = ((ArmorProviderExtensions) stack.getItem())
//					.fabric_getArmorTextureProvider();
//
//			if (provider != null) {
//				return provider.getArmorTexture(entity, stack, slot, secondLayer, suffix, defaultTexture);
//			}
//			if (stack.getItem() instanceof ArmorProvider) {
//				return ((ArmorProvider) stack.getItem()).getArmorTexture(entity, stack, slot, defaultTexture);
//			}
//		}
//
		return defaultTexture;
    }
}