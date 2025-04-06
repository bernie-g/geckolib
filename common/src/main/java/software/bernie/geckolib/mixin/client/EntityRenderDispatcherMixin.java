package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * Injection into the entity render chain to capture additional data for EntityRenderStates and any other
 * pre-render miscellaneous setup not available in time for rendering
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @WrapOperation(method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"))
    public <T extends Entity, S extends EntityRenderState> S geckolib$fillGeoRenderState(EntityRenderer<T, S> renderer, T entity, float partialTick, Operation<S> original,
                                                                                         T entity2, double xOffset, double yOffset, double zOffset, float partialTick2,
                                                                                         PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, EntityRenderer<? super T, S> renderer2) {
        final S renderState = original.call(renderer, entity, partialTick);

        if (renderer instanceof GeoRenderer && renderState instanceof GeoRenderState geoRenderState)
            geoRenderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);

        return renderState;
    }
}
