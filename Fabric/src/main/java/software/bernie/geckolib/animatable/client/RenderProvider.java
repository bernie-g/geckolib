package software.bernie.geckolib.animatable.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.mixins.fabric.ItemRendererAccessor;

/**
 * Internal interface for safely providing a custom renderer instances at runtime.<br>
 * This can be safely instantiated as a new anonymous class inside your {@link Item} class
 */
public interface RenderProvider {
    RenderProvider DEFAULT = new RenderProvider() {};

    static RenderProvider of(ItemStack itemStack) {
        return of(itemStack.getItem());
    }

    static RenderProvider of(Item item) {
        if (item instanceof GeoItem geoItem)
            return (RenderProvider)geoItem.getRenderProvider().get();

        return DEFAULT;
    }

    default BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return ((ItemRendererAccessor)Minecraft.getInstance().getItemRenderer()).getBlockEntityRenderer();
    }


    default Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<LivingEntity> original) {
        HumanoidModel<LivingEntity> replacement = getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);

        if (replacement != original) {
            original.copyPropertiesTo(replacement);

            return replacement;
        }

        return original;
    }

    default HumanoidModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<LivingEntity> original) {
        return original;
    }
}