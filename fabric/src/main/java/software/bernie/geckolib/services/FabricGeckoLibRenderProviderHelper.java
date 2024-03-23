package software.bernie.geckolib.services;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class FabricGeckoLibRenderProviderHelper implements GeckoLibRenderProviderHelper {

    @Override
    public <T extends LivingEntity & GeoAnimatable> HumanoidModel<?> getHumanoidModel(T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel){
        return RenderProvider.of(stack).getHumanoidArmorModel(animatable, stack, slot, defaultModel);
    }

    @Nullable
    @Override
    public GeoModel<?> getGeoModelForItem(Item item){
        if (RenderProvider.of(item).getCustomRenderer() instanceof GeoRenderer<?> geoItemRenderer)
            return geoItemRenderer.getGeoModel();

        return null;
    }

    @Nullable
    @Override
    public GeoModel<?> getGeoModelForArmor(ItemStack stack){
        if (RenderProvider.of(stack).getHumanoidArmorModel(null, stack, null, null) instanceof GeoArmorRenderer<?> armorRenderer)
            return armorRenderer.getGeoModel();

        return null;
    }
}
