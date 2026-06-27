package com.geckolib.mixin.client;

import com.geckolib.object.VanillaModelModifier;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantValue")
@Mixin(Model.class)
public class ModelMixin<S> {
    /// Inject GeckoLib's [VanillaModelModifier]s into the model [Model#setupAnim] call
    @SuppressWarnings("unchecked")
    @Inject(method = "setupAnim", at = @At("TAIL"))
    public void geckolib$callModelSetupCallbacks(S instance, CallbackInfo ci) {
        if (!((Object)this instanceof HumanoidModel) && instance instanceof GeoRenderState renderState)
            //noinspection unchecked,rawtypes
            VanillaModelModifier.runModifierSetup((Model)(Object)this, renderState);
    }
}
