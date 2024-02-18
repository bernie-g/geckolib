package software.bernie.geckolib.services;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public interface GeckoLibRenderProviderHelper {

    <T extends LivingEntity & GeoAnimatable> HumanoidModel<?> getHumanoidModel(T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel);

    @Nullable
    GeoModel<?> getGeoModelForItem(Item item);

    @Nullable
    GeoModel<?> getGeoModelForArmor(ItemStack stack);
}