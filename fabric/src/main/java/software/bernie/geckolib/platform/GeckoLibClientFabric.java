package software.bernie.geckolib.platform;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.service.GeckoLibClient;

/**
 * Fabric service implementation for clientside functionalities
 */
public class GeckoLibClientFabric implements GeckoLibClient {
    /**
     * Helper method for retrieving an (ideally) cached instance of the armor model for a given Item
     * <p>
     * If no custom model applies to this item, the {@code defaultModel} is returned
     */
    @Override
    public @NotNull <S extends HumanoidRenderState & GeoRenderState> HumanoidModel<?> getArmorModelForItem(S renderState, ItemStack stack, EquipmentSlot slot, EquipmentClientInfo.LayerType type, HumanoidModel<S> defaultModel) {
        return GeoRenderProvider.of(stack).getGeoArmorRenderer(renderState, stack, slot, type, defaultModel) instanceof GeoArmorRenderer<?, ?> geoArmorRenderer ? geoArmorRenderer : defaultModel;
    }

    /**
     * Helper method for retrieving an (ideally) cached instance of the GeoModel for the given item
     *
     * @return The GeoModel for the item, or null if not applicable
     */
    @Nullable
    @Override
    public GeoModel<?> getGeoModelForItem(ItemStack item) {
        if (GeoRenderProvider.of(item).getGeoItemRenderer(item) instanceof GeoRenderer<?, ?, ?> geoItemRenderer)
            return geoItemRenderer.getGeoModel();

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
        final HumanoidModel defaultModel = slot == EquipmentSlot.LEGS ? GENERIC_INNER_ARMOR_MODEL.get() : GENERIC_OUTER_ARMOR_MODEL.get();

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
        return itemStack.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(itemStack, defaultColor) : defaultColor;
    }
}
