package com.geckolib.mixin.client;

import com.geckolib.object.VanillaModelModifier;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelFeatureRenderer.class)
public class ModelFeatureRendererMixin {
    /// Inject [VanillaModelModifier#postRenderReset] into the render chain
    @Inject(method = "prepareModel", at = @At("TAIL"))
    public <S> void geckolib$injectModelCleanup(ModelFeatureRenderer.Submit<S> submit, CallbackInfo ci) {
        if (submit.state() instanceof GeoRenderState renderState)
            //noinspection unchecked,rawtypes
            VanillaModelModifier.runModifierCleanup((Model)submit.model(), renderState);
    }
}
