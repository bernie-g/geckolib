package software.bernie.geckolib.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
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
    private int renderedEntities;

    @Inject(method = "renderLevel", at = @At(value = "HEAD"))
    public void geckolib$captureRenderedEntities(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        final int renderedEntityCount = this.renderedEntities;

        MathParser.setVariable(MolangQueries.ACTOR_COUNT, () -> renderedEntityCount);
    }
}
