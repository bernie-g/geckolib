package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoItem;
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

    // Additional non-critical mixin to eliminate console errors & wasted work in retrieving the armour layer texture for GeckoLib models

    @WrapOperation(method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/HumanoidModel;ZFFFLjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;getArmorLocation(Lnet/minecraft/world/item/ArmorItem;ZLjava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            require = 0)
    public ResourceLocation geckolib$cancelTextureRetrievalFabric(HumanoidArmorLayer renderLayer, ArmorItem item, boolean isInnerModel, @Nullable String suffix, Operation<ResourceLocation> original, @Local(argsOnly = true, index = 5) A model) {
        if (model instanceof GeoArmorRenderer geoArmorRenderer)
            return geoArmorRenderer.getTextureLocation((GeoItem)geoArmorRenderer.getAnimatable());

        return original.call(renderLayer, item, isInnerModel, suffix);
    }

    @WrapOperation(method = "renderArmorPiece",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;getArmorResource(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            require = 0)
    public ResourceLocation geckolib$cancelTextureRetrievalForge(HumanoidArmorLayer renderLayer, Entity entity, ItemStack stack, EquipmentSlot equipmentSlot, @Nullable String suffix, Operation<ResourceLocation> original, @Local(argsOnly = true, index = 6) A model) {
        if (model instanceof GeoArmorRenderer geoArmorRenderer)
            return geoArmorRenderer.getTextureLocation((GeoItem)geoArmorRenderer.getAnimatable());

        return original.call(renderLayer, entity, stack, equipmentSlot, suffix);
    }
}