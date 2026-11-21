package software.bernie.geckolib.mixins.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.util.helper.LevelRendererGeckoLibAccess;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements LevelRendererGeckoLibAccess {
    @Unique
    private boolean geckolib$renderingWorld;

    @Override
    public boolean geckolib$isRenderingInWorld() {
        return this.geckolib$renderingWorld;
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    public void geckolib$renderLevelHead(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        this.geckolib$renderingWorld = true;
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    public void geckolib$renderLevelExit(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        this.geckolib$renderingWorld = false;
    }
}
