package com.geckolib.platform;

import com.geckolib.service.GeckoLibClient;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.renderstate.RenderStateExtensions;

/// NeoForge service implementation for clientside functionalities
public class GeckoLibClientNeoForge implements GeckoLibClient {
    /// Helper method for retrieving an (ideally) cached instance of the armor model for a given Item
    ///
    /// If no custom model applies to this item, the `defaultModel` is returned
    @Override
    public <S extends HumanoidRenderState> Model<?> getArmorModelForItem(S entityRenderState, ItemStack stack, EquipmentSlot slot, EquipmentClientInfo.LayerType type, HumanoidModel<S> defaultModel) {
        return IClientItemExtensions.of(stack).getGenericArmorModel(stack, type, defaultModel);
    }

    /// Return the dye value for a given ItemStack, or the defaul value if not present.
    ///
    /// This is split off to allow for handling of loader-specific handling for dyed items
    @Override
    public int getDyedItemColor(ItemStack itemStack, int defaultColor) {
        final int colour = IClientItemExtensions.of(itemStack).getDefaultDyeColor(itemStack);

        return colour == 0 ? defaultColor : colour;
    }

    /// Handle a newly extracted [EntityRenderState] for data extensions
    ///
    /// This is mostly for third-party compatibility reasons
    @Override
    public <E extends Entity, S extends EntityRenderState> void handleEntityRenderStateExtraction(EntityRenderer<E, S> renderer, E entity, S renderState) {
        RenderStateExtensions.onUpdateEntityRenderState(renderer, entity, renderState);
    }
}
