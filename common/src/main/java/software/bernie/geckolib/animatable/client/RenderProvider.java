package software.bernie.geckolib.animatable.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

/**
 * Internal interface for safely providing a custom item and armor renderer instances at runtime
 * <p>
 * This can be safely instantiated as a new anonymous class inside your {@link Item} class
 * <p>
 * This is a functional equivalent to Forge's {@code IClientItemExtensions}. If on Forge, either can be used
 *
 * @see software.bernie.geckolib.renderer.GeoItemRenderer
 * @see software.bernie.geckolib.renderer.GeoArmorRenderer
 */
public interface RenderProvider {
    RenderProvider DEFAULT = new RenderProvider() {};

    /**
     * Get the RenderProvider instance for the given ItemStack, if applicable
     *
     * @param itemStack The ItemStack to get the provider instance for
     * @return The RenderProvider instance for this stack, or a defaulted empty instance if not defined
     */
    static RenderProvider of(ItemStack itemStack) {
        return of(itemStack.getItem());
    }

    /**
     * Get the RenderProvider instance for the given Item, if applicable
     *
     * @param item The ItemStack to get the provider instance for
     * @return The RenderProvider instance for this item, or a defaulted empty instance if not defined
     */
    static RenderProvider of(Item item) {
        if (item instanceof GeoItem geoItem)
            return (RenderProvider)geoItem.getRenderProvider().get();

        return DEFAULT;
    }

    /**
     * Get the cached renderer for this provider, primarily for custom Item rendering
     *
     * @return The cached BEWLR instance for this provider, or null if not applicable
     */
    @Nullable
    default BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return null;
    }

    /**
     * Get the cached HumanoidModel instance for this provider.
     * <p>
     * Normally this would be an instance of {@link software.bernie.geckolib.renderer.GeoArmorRenderer GeoArmorRenderer}
     *
     * @param livingEntity The entity currently wearing the item, if applicable
     * @param itemStack The ItemStack for this provider
     * @param equipmentSlot The slot the ItemStack is currently in, if applicable
     * @param original The base HumanoidModel (usually the default vanilla armor model), if applicable
     * @return The cached HumanoidModel instance for this provider, or the original if not applicable
     */
    default <T extends LivingEntity, A extends HumanoidModel<T>> A getGeckolibArmorModel(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable A original) {
        return original;
    }
}