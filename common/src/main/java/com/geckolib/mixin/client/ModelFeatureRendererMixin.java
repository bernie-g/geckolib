package com.geckolib.mixin.client;

import com.geckolib.object.VanillaModelModifier;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelFeatureRenderer.class)
public class ModelFeatureRendererMixin {
    /// Inject [VanillaModelModifier#postRenderReset] into the render chain
    @Inject(method = "renderModel", at = @At("TAIL"))
    public <S> void geckolib$injectModelCleanup(SubmitNodeStorage.ModelSubmit<S> submit, RenderType renderType, VertexConsumer buffer,
                                                OutlineBufferSource outlineBufferSource, MultiBufferSource.BufferSource crumblingBufferSource, CallbackInfo ci) {
        if (submit.state() instanceof GeoRenderState renderState)
	        //noinspection unchecked,rawtypes
	        VanillaModelModifier.runModifierCleanup((Model)submit.model(), renderState);
    }
}
