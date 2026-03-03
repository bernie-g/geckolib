package com.geckolib.mixin.client;

import com.geckolib.constant.DataTickets;
import com.geckolib.object.VanillaModelModifier;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(Model.class)
public class ModelMixin<S> {
    /// Inject GeckoLib's [VanillaModelModifier]s into the model [Model#setupAnim] call
    @SuppressWarnings("unchecked")
    @Inject(method = "setupAnim", at = @At("TAIL"))
    public void geckolib$callModelSetupCallbacks(S object, CallbackInfo ci) {
        if (object instanceof GeoRenderState renderState) {
            @SuppressWarnings("rawtypes")
            final List<VanillaModelModifier<S, Model<? super S>>> callbacks = (List)renderState.getOrDefaultGeckolibData(DataTickets.VANILLA_MODEL_MODIFIERS, Map.of()).getOrDefault(this, List.of());

            for (VanillaModelModifier<S, Model<? super S>> runnable : callbacks) {
                runnable.setupAnim((Model<? super S>)(Object)this);
            }
        }
    }
}
