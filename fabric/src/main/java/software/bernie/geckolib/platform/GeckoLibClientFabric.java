package software.bernie.geckolib.platform;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
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
    @NotNull
    @Override
    public <E extends LivingEntity & GeoAnimatable, S extends HumanoidRenderState> HumanoidModel<?> getArmorModelForItem(E animatable, S entityRenderState, ItemStack stack, EquipmentSlot slot, EquipmentClientInfo.LayerType type, HumanoidModel<S> defaultModel) {
        return GeoRenderProvider.of(stack).getGeoArmorRenderer(animatable, stack, slot, type, defaultModel) instanceof GeoArmorRenderer<?> geoArmorRenderer ? geoArmorRenderer : defaultModel;
    }

    /**
     * Helper method for retrieving an (ideally) cached instance of the GeoModel for the given item
     *
     * @return The GeoModel for the item, or null if not applicable
     */
    @Nullable
    @Override
    public GeoModel<?> getGeoModelForItem(ItemStack item) {
        if (GeoRenderProvider.of(item).getGeoItemRenderer() instanceof GeoRenderer<?> geoItemRenderer)
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
        final HumanoidModel<?> defaultModel = slot == EquipmentSlot.LEGS ? GENERIC_INNER_ARMOR_MODEL.get() : GENERIC_OUTER_ARMOR_MODEL.get();

        if (GeoRenderProvider.of(armour).getGeoArmorRenderer(null, armour, slot, type, defaultModel) instanceof GeoArmorRenderer<?> armorRenderer)
            return armorRenderer.getGeoModel();

        return null;
    }
}
