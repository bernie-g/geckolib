package software.bernie.geckolib.services;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class NeoForgeGeckoLibRenderProviderHelper implements GeckoLibRenderProviderHelper {
    @Override
    public <T extends LivingEntity & GeoAnimatable> HumanoidModel<?> getHumanoidModel(T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel){
        Item item = stack.getItem();
        HumanoidModel<?> model = IClientItemExtensions.of(item).getHumanoidArmorModel(animatable, stack, slot, defaultModel);

        if (model == defaultModel) {
            return RenderProvider.of(item).getHumanoidArmorModel(animatable, stack, slot, defaultModel);
        }

        return model; //Return the one NeoForge has
    }

    @Nullable
    @Override
    public GeoModel<?> getGeoModelForItem(Item item){
        if(IClientItemExtensions.of(item).getCustomRenderer() instanceof GeoRenderer<?> geoRenderer)
            return geoRenderer.getGeoModel();

        if (RenderProvider.of(item).getCustomRenderer() instanceof GeoRenderer<?> geoRenderer)
            return geoRenderer.getGeoModel();

        return null;
    }

    @Nullable
    @Override
    public GeoModel<?> getGeoModelForArmor(ItemStack stack){
        if (IClientItemExtensions.of(stack).getHumanoidArmorModel(null, stack, null, null) instanceof GeoArmorRenderer<?> armorRenderer)
            return armorRenderer.getGeoModel();

        if (RenderProvider.of(stack).getHumanoidArmorModel(null, stack, null, null) instanceof GeoArmorRenderer<?> armorRenderer)
            return armorRenderer.getGeoModel();

        return null;
    }
}
