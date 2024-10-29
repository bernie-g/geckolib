package software.bernie.geckolib.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.loading.math.MolangQueries;

/**
 * Capture pre-render data for Molang queries
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private int visibleEntityCount;

    @Inject(method = "renderLevel", at = @At(value = "HEAD"))
    public void geckolib$captureRenderedEntities(CallbackInfo ci) {
        final int renderedEntityCount = this.visibleEntityCount;

        MathParser.setVariable(MolangQueries.ACTOR_COUNT, () -> renderedEntityCount);
    }
}
