package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> extends RenderLayer<S, M> {
    public HumanoidArmorLayerMixin(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    /**
     * Injection into the render point for armour on HumanoidModels (Players, Zombies, etc) to defer to GeckoLib item-armor rendering as applicable
     * <p>
     * Does nothing if GeckoLib has nothing to handle for the given arguments
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"))
    public boolean geckolib$wrapArmorPieceRender(HumanoidArmorLayer<S, M, A> layer, PoseStack poseStack, SubmitNodeCollector renderTasks, ItemStack stack, EquipmentSlot slot, int packedLight, S entityRenderState) {
        return entityRenderState instanceof HumanoidRenderState && !GeoArmorRenderer.tryRenderGeoArmorPiece(
                (renderState, equipmentSlot) -> (HumanoidModel)layer.getArmorModel((S)renderState, equipmentSlot),
                poseStack, renderTasks, stack, slot, packedLight, (HumanoidRenderState & GeoRenderState)entityRenderState);
    }
}