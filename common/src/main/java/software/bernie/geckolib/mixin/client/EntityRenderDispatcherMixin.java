package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.renderer.GeoEntityRenderState;

/**
 * Injection into the entity render chain to capture additional data for EntityRenderStates and any other
 * pre-render miscellaneous setup
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @WrapOperation(method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"))
    public <T extends Entity, S extends EntityRenderState> S geckolib$fillGeoRenderState(EntityRenderer<T, S> renderer, T entity, float partialTick, Operation<S> original) {
        final S renderState = original.call(renderer, entity, partialTick);

        if (renderState instanceof GeoEntityRenderState geoRenderState) {
            geoRenderState.geckolib$setEntity(entity);
            geoRenderState.geckolib$setPartialTick(partialTick);
        }

        return renderState;
    }
}
