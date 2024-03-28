package software.bernie.geckolib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.client.RenderProvider;

/**
 * Injection into the BEWLR rendering point to defer to GeckoLib item rendering if applicable
 * <p>
 * Cancels the remainder of the render call if GeckoLib renders, otherwise does nothing
 */
@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    public void geckolib$renderGeckolibItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci) {
        final BlockEntityWithoutLevelRenderer geckolibRenderer = RenderProvider.of(stack).getCustomRenderer();

        if (geckolibRenderer != null) {
            geckolibRenderer.renderByItem(stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);

            ci.cancel();
        }
    }
}
