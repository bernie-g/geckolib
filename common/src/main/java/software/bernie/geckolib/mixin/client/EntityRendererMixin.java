package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Mixin(value = EntityRenderer.class, priority = 5000)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    /**
     * Override the maximum distance a GeckoLib entity's nameplate can render at, since vanilla caps it at 64 blocks
     */
    @ModifyConstant(method = "extractRenderState", constant = @Constant(doubleValue = 4096.0F), require = 0)
    public double modifyMaxNameplateDistance(double constant) {
        return (Object)this instanceof GeoEntityRenderer<?, ?> ? 256 * 256 : constant;
    }

    /**
     * Injection mixin to allow for capture of data for {@link GeoRenderState}s for {@link GeoArmorRenderer}s,
     * given that they never normally receive the entity context
     */
    @WrapMethod(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;")
    public S geckolib$captureDataForArmorLayer(T entity, float partialTick, Operation<S> original) {
        S renderState = original.call(entity, partialTick);

        if (renderState instanceof HumanoidRenderState && (Object)this instanceof LivingEntityRenderer livingRenderer) {
            for (Object layer : livingRenderer.layers) {
                if (layer instanceof HumanoidArmorLayer armorLayer) {
                    GeoArmorRenderer.captureRenderStates(geckolib$castRenderState(renderState), (LivingEntity)entity, partialTick, armorLayer,
                                                         slot -> geckolib$castRenderState(slot == EquipmentSlot.HEAD ? renderState : original.call(entity, partialTick)));

                    break;
                }
            }
        }

        return renderState;
    }

    /**
     * Sugar method for blind-casting RenderStates to GeckoLib-supported generic types
     */
    @Unique
    private static <R extends HumanoidRenderState & GeoRenderState> R geckolib$castRenderState(EntityRenderState renderState) {
        return (R)renderState;
    }
}
