package com.geckolib.mixin.client;

import com.geckolib.constant.DataTickets;
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

import java.util.List;
import java.util.Map;

@Mixin(ModelFeatureRenderer.class)
public class ModelFeatureRendererMixin {
    /// Inject [VanillaModelModifier#postRenderReset] into the render chain
    @Inject(method = "renderModel", at = @At("TAIL"))
    public <S> void geckolib$injectModelCleanup(SubmitNodeStorage.ModelSubmit<S> submit, RenderType renderType, VertexConsumer buffer,
                                                OutlineBufferSource outlineBufferSource, MultiBufferSource.BufferSource crumblingBufferSource, CallbackInfo ci) {
        if (submit.state() instanceof GeoRenderState renderState) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            final List<VanillaModelModifier<S, Model<? super S>>> callbacks = (List)renderState.getOrDefaultGeckolibData(DataTickets.VANILLA_MODEL_MODIFIERS, Map.of()).getOrDefault(submit.model(), List.of());

            for (VanillaModelModifier<S, Model<? super S>> runnable : callbacks) {
                runnable.postRenderReset(submit.model());
            }
        }
    }
}
