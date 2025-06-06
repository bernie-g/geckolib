package software.bernie.geckolib.mixins.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.animatable.client.RenderProvider;

/**
 * Render hook to inject GeckoLib's BEWLR rendering callback
 */
@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @WrapOperation(method = "render", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;renderByItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"))
    public void cancelGeckolibRender(BlockEntityWithoutLevelRenderer defaultRenderer, ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Operation<Void> original) {
        final BlockEntityWithoutLevelRenderer renderer = RenderProvider.of(itemStack).getCustomRenderer();

        if (renderer != RenderProvider.DEFAULT.getCustomRenderer())
            defaultRenderer = renderer;

        original.call(defaultRenderer, itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
