package software.bernie.geckolib.animatable.client;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
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
     * Normally this would be an instance of {@code GeoItemRenderer}
     *
     * @return The cached {@code GeoItemRenderer} instance for this provider, or null if not applicable
     */
    default @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
        return null;
    }

    /**
     * Get the cached {@link GeoArmorRenderer} instance for this provider.
     *
     * @param itemStack The ItemStack for this provider
     * @param equipmentSlot The slot the ItemStack would be rendered in
     * @return The cached GeoArmorRenderer instance for this provider, or null if no armor renderer applies
     */
    default @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
        return null;
    }
}