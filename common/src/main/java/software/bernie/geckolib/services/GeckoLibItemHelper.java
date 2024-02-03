package software.bernie.geckolib.services;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

public interface GeckoLibItemHelper {

    <T extends LivingEntity & GeoAnimatable> HumanoidModel<?> getHumanoidModel(T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel);
}
