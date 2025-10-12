package software.bernie.geckolib.mixin.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.renderer.base.GeckolibItemSpecialRenderer;

@Mixin(SpecialModelRenderers.class)
public class SpecialModelRenderersMixin {
    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> ID_MAPPER;

    /**
     * Inject GeckoLib's custom item model renderer into the vanilla map of special renderers
     */
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void geckolib$addSpecialRenderer(CallbackInfo ci) {
        ID_MAPPER.put(GeckoLibConstants.id("geckolib"), GeckolibItemSpecialRenderer.Unbaked.MAP_CODEC);
    }
}