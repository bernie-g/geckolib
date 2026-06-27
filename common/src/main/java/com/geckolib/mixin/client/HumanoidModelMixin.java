package com.geckolib.mixin.client;

import com.geckolib.object.VanillaModelModifier;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantValue")
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends HumanoidRenderState> {
	/// Inject GeckoLib's [VanillaModelModifier]s into the model [HumanoidModel#setupAnim(HumanoidRenderState)] call
	@SuppressWarnings("unchecked")
	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
	public void geckolib$callModelSetupCallbacks(T instance, CallbackInfo ci) {
		if (!((Object)this instanceof PlayerModel) && instance instanceof GeoRenderState renderState)
			//noinspection unchecked,rawtypes
			VanillaModelModifier.runModifierSetup((Model)(Object)this, renderState);
	}
}
