package software.bernie.geckolib.mixin.client;

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
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/extensions/common/IClientItemExtensions;getCustomRenderer()Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;"))
    private BlockEntityWithoutLevelRenderer geckolib$wrapGeoItemRenderer(IClientItemExtensions clientItemExtensions, Operation<BlockEntityWithoutLevelRenderer> callback, ItemStack stack, ItemDisplayContext context,
                                                                         boolean isLeftHand, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BakedModel bakedModel) {
        final BlockEntityWithoutLevelRenderer renderer = callback.call(clientItemExtensions);

        if (renderer == Minecraft.getInstance().getItemRenderer().blockEntityRenderer)
            return RenderProvider.of(stack).getCustomRenderer();

        return renderer;
    }
}
