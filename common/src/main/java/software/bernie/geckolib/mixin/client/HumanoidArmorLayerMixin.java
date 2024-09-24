package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.Color;

/**
 * Injection into the render point for armour on HumanoidModels (Players, Zombies, etc) to defer to GeckoLib item-armor rendering as applicable
 * <p>
 * Does nothing if GeckoLib has nothing to handle for the given arguments
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> {
    @ModifyExpressionValue(
            method = "renderArmorPiece",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack geckolib$captureItemBySlot(ItemStack original, @Share("item_by_slot") LocalRef<ItemStack> itemBySlotRef) {
        itemBySlotRef.set(original);
        return original;
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;usesInnerModel(Lnet/minecraft/world/entity/EquipmentSlot;)Z"), cancellable = true)
    private void geckolib$renderGeckoLibModel(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot equipmentSlot, int packedLight, A baseModel, CallbackInfo ci, @Share("item_by_slot") LocalRef<ItemStack> itemBySlotRef) {
        final ItemStack stack = itemBySlotRef.get();
        final HumanoidModel<?> geckolibModel = GeoRenderProvider.of(stack).getGeoArmorRenderer(entity, stack, equipmentSlot, baseModel);

        if (geckolibModel != null) {
            if (geckolibModel instanceof GeoArmorRenderer<?> geoArmorRenderer)
                geoArmorRenderer.prepForRender(entity, stack, equipmentSlot, baseModel);

            baseModel.copyPropertiesTo((A)geckolibModel);
            geckolibModel.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());
            ci.cancel();
        }
    }

    @Inject(method = "renderArmorPiece", at = @At("TAIL"))
    private void geckolib$cleanupPostRender(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot equipmentSlot, int packedLight, A baseModel, CallbackInfo ci) {
        final ItemStack stack = entity.getItemBySlot(equipmentSlot);

        if (GeoRenderProvider.of(stack).getGeoArmorRenderer(entity, stack, equipmentSlot, baseModel) instanceof GeoArmorRenderer geoArmorRenderer)
            geoArmorRenderer.doArmourPostRenderCleanup();
    }
}