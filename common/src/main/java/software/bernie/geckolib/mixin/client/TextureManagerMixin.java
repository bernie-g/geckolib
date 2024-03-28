package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.cache.texture.AnimatableTexture;

/**
 * Injection into TextureManager's access point for runtime-derived textures to allow GeckoLib to swap them out with {@link AnimatableTexture} for animated texture purposes
 * <p>
 * Because AnimatedTexture extends {@link net.minecraft.client.renderer.texture.SimpleTexture SimpleTexture}, the replacement should be seamless
 */
@Mixin(value = TextureManager.class, priority = 2000)
public abstract class TextureManagerMixin {
	@WrapOperation(method = "getTexture(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AbstractTexture;",
			at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/SimpleTexture;"),
			require = 0)
	private SimpleTexture geckolib$replaceAnimatableTexture(ResourceLocation location, Operation<SimpleTexture> original) {
		return new AnimatableTexture(location);
	}
}
