package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/**
 * Injection into the render point for armour on HumanoidModels (Players, Zombies, etc) to defer to GeckoLib item-armor rendering as applicable
 * <p>
 * Does nothing if GeckoLib has nothing to handle for the given arguments
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> {
    @Shadow
    protected abstract void setPartVisibility(A baseModel, EquipmentSlot equipmentSlot);

    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            require = 0,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V"))
    public boolean geckolib$wrapArmorPieceRender(HumanoidArmorLayer<S, M, A> renderLayer, PoseStack poseStack, MultiBufferSource bufferSource, ItemStack stack, EquipmentSlot equipmentSlot, int packedLight, A baseModel,
                                                 PoseStack poseStack2, MultiBufferSource bufferSource2, int packedLight2, S humanoidRenderState, float netHeadYaw, float headPitch) {
        return !GeoArmorRenderer.tryRenderGeoArmorPiece(poseStack, bufferSource, humanoidRenderState, stack, equipmentSlot, renderLayer.getParentModel(), baseModel, packedLight, netHeadYaw,
                                                        headPitch, this::setPartVisibility);
    }

    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            require = 0,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", remap = false))
    public boolean geckolib$wrapArmorPieceRenderForge(HumanoidArmorLayer<S, M, A> renderLayer, PoseStack poseStack, MultiBufferSource bufferSource, ItemStack stack, EquipmentSlot equipmentSlot, int packedLight, A baseModel,
                                                      S humanoidRenderState, PoseStack poseStack2, MultiBufferSource bufferSource2, int packedLight2, S humanoidRenderState2, float netHeadYaw, float headPitch) {
        return !GeoArmorRenderer.tryRenderGeoArmorPiece(poseStack, bufferSource, humanoidRenderState, stack, equipmentSlot, renderLayer.getParentModel(), baseModel, packedLight, netHeadYaw,
                                                    headPitch, this::setPartVisibility);
    }
}