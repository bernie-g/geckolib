package software.bernie.geckolib.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import software.bernie.geckolib.animatable.client.RenderProvider;

@Mixin(HumanoidArmorLayer.class)
public class MixinHumanoidArmorLayer {
    @ModifyVariable(method = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V",
            at = @At(value = "STORE", opcode = Opcodes.ASTORE),
            ordinal = 0, index = 10, name = "model", print = false)

    private Model geckolib_geoItemReplacement(Model model, PoseStack pPoseStack, MultiBufferSource pBuffer, LivingEntity pLivingEntity, EquipmentSlot pSlot, int pPackedLight, HumanoidModel<LivingEntity> original) {
        if (model == original) { //The model was not replaced by something (some IClientExtensions)
            ItemStack itemstack = pLivingEntity.getItemBySlot(pSlot);
            Item item = itemstack.getItem();
            return RenderProvider.of(item).getGenericArmorModel(pLivingEntity, itemstack, pSlot, original); //In the event this is not a GeoItem, the original is returned.
        }
        return model;
    }
}
