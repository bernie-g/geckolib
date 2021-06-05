package software.bernie.geckolib3;

import org.jetbrains.annotations.Nullable;

public interface ArmorProviderExtensions {
	@Nullable
	ArmorRenderingRegistry.ModelProvider fabric_getArmorModelProvider();

	@Nullable
	ArmorRenderingRegistry.TextureProvider fabric_getArmorTextureProvider();

	void fabric_setArmorModelProvider(@Nullable ArmorRenderingRegistry.ModelProvider provider);

	void fabric_setArmorTextureProvider(@Nullable ArmorRenderingRegistry.TextureProvider provider);
}