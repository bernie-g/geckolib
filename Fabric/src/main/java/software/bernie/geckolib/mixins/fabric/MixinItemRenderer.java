package software.bernie.geckolib.mixins.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.client.RenderProvider;

/**
 * Render hook to inject GeckoLib's ISTER rendering callback
 */
@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    // Done in two parts so that the cancellation of the vanilla render can fail-safe without breaking functionality

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;renderByItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"))
    public void itemModelHook(ItemStack itemStack, ItemDisplayContext transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
       final BlockEntityWithoutLevelRenderer renderer = RenderProvider.of(itemStack).getCustomRenderer();

       if (renderer != RenderProvider.DEFAULT.getCustomRenderer())
           renderer.renderByItem(itemStack, transformType, poseStack, multiBufferSource, i, j);
    }

    @WrapOperation(method = "render", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;renderByItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"))
    public void cancelRender(BlockEntityWithoutLevelRenderer renderer, ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Operation<Void> original) {
        if (RenderProvider.of(itemStack).getCustomRenderer() == renderer)
            renderer.renderByItem(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
        else
            original.call(renderer, itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
