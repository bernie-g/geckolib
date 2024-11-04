package software.bernie.geckolib.animatable.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper class for GeoRenderProvider, to allow an externally held GeoRenderProvider to be used as the basis for this one
 * <p>
 * Should be anonymously instantiated for side-safety
 */
public interface DeferredGeoRenderProvider extends GeoRenderProvider {
    /**
     * Return the externally held GeoRenderProvider.
     * <p>
     * Normally this would be stored as a field in your item class
     */
    MutableObject<GeoRenderProvider> getRenderProvider();

    @Override
    @Nullable
    default BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        return getRenderProvider().getValue().getGeoItemRenderer();
    }

    @Override
    @Nullable
    default <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
        return getRenderProvider().getValue().getGeoArmorRenderer(livingEntity, itemStack, equipmentSlot, original);
    }
}