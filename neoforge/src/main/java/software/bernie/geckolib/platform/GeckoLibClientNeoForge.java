package software.bernie.geckolib.platform;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.service.GeckoLibClient;

/**
 * NeoForge service implementation for clientside functionalities
 */
public class GeckoLibClientNeoForge implements GeckoLibClient {
    /**
     * Helper method for retrieving an (ideally) cached instance of the armor model for a given Item
     * <p>
     * If no custom model applies to this item, the {@code defaultModel} is returned
     */
    @NotNull
    @Override
    public <S extends HumanoidRenderState & GeoRenderState> Model getArmorModelForItem(S entityRenderState, ItemStack stack, EquipmentSlot slot, EquipmentClientInfo.LayerType type, HumanoidModel<S> defaultModel) {
        Item item = stack.getItem();
        Model model = IClientItemExtensions.of(item).getHumanoidArmorModel(stack, type, defaultModel);

        if (model == defaultModel && GeoRenderProvider.of(item).getGeoArmorRenderer(entityRenderState, stack, slot, type, defaultModel) instanceof GeoArmorRenderer<?, ?> geoArmorRenderer)
            return geoArmorRenderer;

        return model;
    }

    /**
     * Helper method for retrieving an (ideally) cached instance of the GeoModel for the given item
     *
     * @return The GeoModel for the item, or null if not applicable
     */
    @Nullable
    @Override
    public GeoModel<?> getGeoModelForItem(ItemStack item) {
        if (GeoRenderProvider.of(item).getGeoItemRenderer() instanceof GeoRenderer<?, ?, ?> geoRenderer)
            return geoRenderer.getGeoModel();

        return null;
    }

    /**
     * Helper method for retrieving an (ideally) cached instance of the GeoModel for the given item's armor renderer
     *
     * @return The GeoModel for the item, or null if not applicable
     */
    @Nullable
    @Override
    public GeoModel<?> getGeoModelForArmor(ItemStack armour, EquipmentSlot slot, EquipmentClientInfo.LayerType type) {
        final HumanoidModel<?> defaultModel = slot == EquipmentSlot.LEGS ? GENERIC_INNER_ARMOR_MODEL.get() : GENERIC_OUTER_ARMOR_MODEL.get();

        if (IClientItemExtensions.of(armour).getHumanoidArmorModel(armour, type, defaultModel) instanceof GeoArmorRenderer<?, ?> armorRenderer)
            return armorRenderer.getGeoModel();

        if (GeoRenderProvider.of(armour).getGeoArmorRenderer(null, armour, slot, type, defaultModel) instanceof GeoArmorRenderer<?, ?> armorRenderer)
            return armorRenderer.getGeoModel();

        return null;
    }

    /**
     * Return the dye value for a given ItemStack, or the defaul value if not present.
     * <p>
     * This is split off to allow for handling of loader-specific handling for dyed items
     */
    @Override
    public int getDyedItemColor(ItemStack itemStack, int defaultColor) {
        final int colour = IClientItemExtensions.of(itemStack).getDefaultDyeColor(itemStack);

        return colour == 0 ? defaultColor : colour;
    }
}
