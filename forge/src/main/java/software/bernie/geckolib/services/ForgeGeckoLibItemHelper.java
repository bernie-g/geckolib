package software.bernie.geckolib.services;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

public class ForgeGeckoLibItemHelper implements GeckoLibItemHelper {
    @Override
    public <T extends LivingEntity & GeoAnimatable> HumanoidModel<?> getHumanoidModel(T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel){
        Item item = stack.getItem();
        HumanoidModel<?> model = IClientItemExtensions.of(item).getHumanoidArmorModel(animatable, stack, slot, defaultModel);

        if (model == defaultModel) {
            return RenderProvider.of(item).getHumanoidArmorModel(animatable, stack, slot, defaultModel);
        }

        return model; //Return the one Forge has
    }
}
