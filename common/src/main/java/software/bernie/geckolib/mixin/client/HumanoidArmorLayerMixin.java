package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/**
 * Injection into the render point for armour on HumanoidModels (Players, Zombies, etc) to defer to GeckoLib item-armor rendering as applicable
 * <p>
 * Does nothing if GeckoLib has nothing to handle for the given arguments
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> {
    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    public void geckolib$replaceArmorModel(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot equipmentSlot, int packedLight, A baseModel, CallbackInfo ci, @Local(argsOnly = true, index = 6) LocalRef<HumanoidModel<T>> baseModelSetter) {
        final ItemStack stack = entity.getItemBySlot(equipmentSlot);
        final HumanoidModel<?> geckolibModel = GeoRenderProvider.of(stack).getGeoArmorRenderer(entity, stack, equipmentSlot, baseModel);

        if (geckolibModel != null) {
            if (geckolibModel instanceof GeoArmorRenderer<?> geoArmorRenderer)
                geoArmorRenderer.prepForRender(entity, stack, equipmentSlot, baseModel);

            final A newModel = (A)geckolibModel;

            baseModel.copyPropertiesTo(newModel);
            baseModelSetter.set(newModel);
        }
    }
}