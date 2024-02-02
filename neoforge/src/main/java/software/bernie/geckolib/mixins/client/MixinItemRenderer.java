package software.bernie.geckolib.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.animatable.client.RenderProvider;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @WrapOperation(method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/extensions/common/IClientItemExtensions;getCustomRenderer()Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;"))
    private BlockEntityWithoutLevelRenderer geckolib_getCustomRenderer(IClientItemExtensions instance,
                                                                       Operation<BlockEntityWithoutLevelRenderer> original, ItemStack pItemStack, ItemDisplayContext pDisplayContext,
                                                                       boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay,
                                                                       BakedModel pModel){

        BlockEntityWithoutLevelRenderer renderer = original.call(instance);
        if (renderer == Minecraft.getInstance().getItemRenderer().blockEntityRenderer) {
            return RenderProvider.of(pItemStack).getCustomRenderer();
        }
        return renderer;
    }
}
