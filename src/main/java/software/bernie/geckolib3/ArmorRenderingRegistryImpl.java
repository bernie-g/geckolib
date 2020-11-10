package software.bernie.geckolib3;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public final class ArmorRenderingRegistryImpl {
    private ArmorRenderingRegistryImpl() {
    }

    public static void registerModel(ArmorRenderingRegistry.ModelProvider provider, Iterable<Item> items) {
        Objects.requireNonNull(items);

        for (Item item : items) {
            Objects.requireNonNull(item);

            ((ArmorProviderExtensions) item).fabric_setArmorModelProvider(provider);
        }
    }

    public static void registerTexture(ArmorRenderingRegistry.TextureProvider provider, Iterable<Item> items) {
        Objects.requireNonNull(items);

        for (Item item : items) {
            Objects.requireNonNull(item);

            ((ArmorProviderExtensions) item).fabric_setArmorTextureProvider(provider);
        }
    }

    public static BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel) {
        if (!stack.isEmpty()) {
            ArmorRenderingRegistry.ModelProvider provider = ((ArmorProviderExtensions) stack.getItem()).fabric_getArmorModelProvider();
            if (provider != null) {
                return provider.getArmorModel(entity, stack, slot, defaultModel);
            }
            if (stack.getItem() instanceof ArmorProvider) {
                return ((ArmorProvider) stack.getItem()).getArmorModel(entity, stack, slot, defaultModel);
            }
        }

        return defaultModel;
    }

    public static String getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, String defaultTexture) {
        if (!stack.isEmpty()) {
            ArmorRenderingRegistry.TextureProvider provider = ((ArmorProviderExtensions) stack.getItem()).fabric_getArmorTextureProvider();

            if (provider != null) {
                return provider.getArmorTexture(entity, stack, slot, defaultTexture);
            }
            if (stack.getItem() instanceof ArmorProvider) {
                return ((ArmorProvider) stack.getItem()).getArmorTexture(entity, stack, slot, defaultTexture);
            }
        }

        return null;
    }
}