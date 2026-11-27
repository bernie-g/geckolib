package software.bernie.geckolib.mixins.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.client.RenderProvider;

import java.util.Optional;

/**
 * Render hook for injecting GeckoLib's armor rendering functionalities
 */
@Mixin(value = HumanoidArmorLayer.class, priority = 700)
public abstract class MixinHumanoidArmorLayer<T extends LivingEntity, A extends HumanoidModel<T>> {
    @Unique
    private LivingEntity gl_storedEntity;
    @Unique
    private EquipmentSlot gl_storedSlot;
    @Unique
    private ItemStack gl_storedItemStack;
    @Unique
    private @Nullable A gl_replacedModel;

    @Inject(method = "renderArmorPiece", at = @At(value = "HEAD"))
    public void armorModelHook(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci) {
        this.gl_storedEntity = livingEntity;
        this.gl_storedSlot = equipmentSlot;
        this.gl_storedItemStack = livingEntity.getItemBySlot(equipmentSlot);
        this.gl_replacedModel = null;
    }

    @WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/HumanoidModel;ZFFFLjava/lang/String;)V"))
    public void geckolib$renderArmorModel(HumanoidArmorLayer<?, ?, ?> instance, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ArmorItem armorItem, A humanoidModel,
                                          boolean withGlint, float red, float green, float blue, @Nullable String armorSuffix, Operation<Void> original) {
        if (this.gl_replacedModel != null)
            return;

        final A armorModel = (A)RenderProvider.of(this.gl_storedItemStack).getGenericArmorModel(this.gl_storedEntity, this.gl_storedItemStack, this.gl_storedSlot, (HumanoidModel<LivingEntity>)humanoidModel);

        if (armorModel != humanoidModel)
            this.gl_replacedModel = armorModel;

        original.call(instance, poseStack, bufferSource, packedLight, armorItem, armorModel, withGlint, red, green, blue, armorSuffix);
    }

    @WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/armortrim/ArmorTrim;getTrim(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"))
    public Optional<ArmorTrim> geckolib$disableTrimRender(RegistryAccess registryAccess, ItemStack itemStack, Operation<Optional<ArmorTrim>> original) {
        Optional<ArmorTrim> trim = original.call(registryAccess, itemStack);

        return this.gl_replacedModel != null ? Optional.empty() : trim;
    }

    @WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z"))
    public boolean geckolib$disableGlintRender(ItemStack instance, Operation<Boolean> original) {
        if (!original.call(instance))
            return false;

        return this.gl_replacedModel == null;
    }
}
