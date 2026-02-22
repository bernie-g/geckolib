package com.geckolib.platform;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.service.GeckoLibClient;

/**
 * Forge service implementation for clientside functionalities
 */
public final class GeckoLibClientForge implements GeckoLibClient {
    /**
     * Helper method for retrieving an (ideally) cached instance of the armor model for a given Item
     * <p>
     * If no custom model applies to this item, the {@code defaultModel} is returned
     */
    @Override
    public <S extends HumanoidRenderState & GeoRenderState> @NonNull Model<?> getArmorModelForItem(S entityRenderState, ItemStack stack, EquipmentSlot slot, EquipmentClientInfo.LayerType type, HumanoidModel<S> defaultModel) {
        return IClientItemExtensions.of(stack).getGenericArmorModel(entityRenderState, stack, slot, defaultModel);
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
