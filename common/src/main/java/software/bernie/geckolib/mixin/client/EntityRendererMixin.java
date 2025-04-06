package software.bernie.geckolib.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Override the maximum distance a GeckoLib entity's nameplate can render at, since vanilla caps it at 64 blocks
 */
@Mixin(value = EntityRenderer.class, priority = 5000)
public class EntityRendererMixin {
    @ModifyConstant(method = "extractRenderState", constant = @Constant(doubleValue = 4096.0F), require = 0)
    public double modifyMaxNameplateDistance(double constant) {
        return ((Object)this) instanceof GeoEntityRenderer<?, ?> ? 256 * 256 : constant;
    }
}
