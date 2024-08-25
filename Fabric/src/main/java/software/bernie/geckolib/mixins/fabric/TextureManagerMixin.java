package software.bernie.geckolib.mixins.fabric;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.cache.texture.AnimatableTexture;

import java.util.Map;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
	@Shadow @Final private Map<ResourceLocation, AbstractTexture> byPath;

	@Shadow public abstract void register(ResourceLocation resourceLocation, AbstractTexture abstractTexture);

	@Inject(method = "getTexture(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AbstractTexture;", at = @At("HEAD"))
	private void wrapAnimatableTexture(ResourceLocation path, CallbackInfoReturnable<AbstractTexture> callback) {
		AbstractTexture existing = this.byPath.get(path);

		if (existing == null) {
			AnimatableTexture animatableTexture = new AnimatableTexture(path);

			register(path, animatableTexture);

			if (!animatableTexture.isAnimated())
				this.byPath.remove(path);
		}
	}
}
