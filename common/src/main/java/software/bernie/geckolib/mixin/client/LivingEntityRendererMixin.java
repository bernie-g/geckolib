package software.bernie.geckolib.mixin.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.List;

/**
 * Injection mixin to allow for capture of data for {@link GeoRenderState}s for {@link GeoArmorRenderer}s,
 * given that they never normally receive the entity context
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Shadow
    @Final
    protected List<RenderLayer<S, M>> layers;

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL"))
    public void geckolib$captureDataForArmorLayer(T entity, S livingRenderState, float partialTick, CallbackInfo ci) {
        if (!(livingRenderState instanceof HumanoidRenderState) || !(livingRenderState instanceof GeoRenderState geoRenderState))
            return;

        for (RenderLayer<S, M> layer : this.layers) {
            if (layer instanceof HumanoidArmorLayer) {
                GeoArmorRenderer.captureRenderStates((HumanoidRenderState & GeoRenderState)geoRenderState, entity, partialTick);

                return;
            }
        }
    }
}
