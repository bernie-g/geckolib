package software.bernie.geckolib.animatable.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Internal interface for safely providing a custom item and armor renderer instances at runtime
 * <p>
 * This can be safely instantiated as a new anonymous class inside your {@link Item} class
 * <p>
 * This is a functional equivalent to Forge's {@code IClientItemExtensions}. If on Forge, either can be used
 *
 * @see software.bernie.geckolib.renderer.GeoItemRenderer GeoItemRenderer
 * @see software.bernie.geckolib.renderer.GeoArmorRenderer GeoArmorRenderer
 */
public interface GeoRenderProvider {
    GeoRenderProvider DEFAULT = new GeoRenderProvider() {};

    /**
     * Get the GeoRenderProvider instance for the given ItemStack, if applicable
     *
     * @param itemStack The ItemStack to get the provider instance for
     * @return The GeoRenderProvider instance for this stack, or a defaulted empty instance if not defined
     */
    static GeoRenderProvider of(ItemStack itemStack) {
        return of(itemStack.getItem());
    }

    /**
     * Get the GeoRenderProvider instance for the given Item, if applicable
     *
     * @param item The ItemStack to get the provider instance for
     * @return The GeoRenderProvider instance for this item, or a defaulted empty instance if not defined
     */
    static GeoRenderProvider of(Item item) {
        if (item instanceof GeoItem geoItem)
            return (GeoRenderProvider)geoItem.getRenderProvider();

        return DEFAULT;
    }

    /**
     * Get the cached {@link GeoItemRenderer} instance for this provider.
     * <p>
     * Normally this would be an instance of {@link GeoItemRenderer}
     *
     * @return The cached BEWLR instance for this provider, or null if not applicable
     */
    @Nullable
    default GeoItemRenderer<?> getGeoItemRenderer() {
        return null;
    }

    /**
     * Get the cached {@link GeoArmorRenderer} instance for this provider.
     *
     * @param renderState The {@link HumanoidRenderState} for the current render pass, or null if one isn't available
     * @param itemStack The ItemStack for this provider
     * @param equipmentSlot The slot the ItemStack would be rendered in
     * @param type The equipment model type to retrieve
     * @param original The base HumanoidModel (usually the default vanilla armor model), or null if one isn't available
     * @return The cached GeoArmorRenderer instance for this provider, or null if no armor renderer applies
     */
    @Nullable
    default <S extends HumanoidRenderState> GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable S renderState, ItemStack itemStack, EquipmentSlot equipmentSlot, EquipmentClientInfo.LayerType type, @Nullable HumanoidModel<S> original) {
        return null;
    }
}