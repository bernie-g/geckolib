package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.util.InternalUtil;

/**
 * Injection into the render point for armour on HumanoidModels (Players, Zombies, etc) to defer to GeckoLib item-armor rendering as applicable
 * <p>
 * Does nothing if GeckoLib has nothing to handle for the given arguments
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
    @Shadow
    protected abstract void setPartVisibility(A baseModel, EquipmentSlot equipmentSlot);

    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V"))
    public boolean geckolib$wrapArmorPieceRender(HumanoidArmorLayer<T, M, A> renderLayer, PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot equipmentSlot, int packedLight, A baseModel,
                                                 PoseStack poseStack2, MultiBufferSource bufferSource2, int packedLight2, T entity2, float limbSwing, float limbSwingAmount, float partialTick, float lerpedTickCount, float netHeadYaw, float headPitch) {
        return !InternalUtil.tryRenderGeoArmorPiece(poseStack, bufferSource, entity, entity.getItemBySlot(equipmentSlot), equipmentSlot, renderLayer.getParentModel(), baseModel, partialTick, packedLight, limbSwing, limbSwingAmount, lerpedTickCount, netHeadYaw, headPitch, this::setPartVisibility);
    }
}