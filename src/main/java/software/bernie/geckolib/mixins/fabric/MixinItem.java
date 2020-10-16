package software.bernie.geckolib.mixins.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.ArmorProviderExtensions;
import software.bernie.geckolib.ArmorRenderingRegistry;

@Mixin(Item.class)
public class MixinItem implements ArmorProviderExtensions {
	@Unique
	@Environment(EnvType.CLIENT)
	private ArmorRenderingRegistry.ModelProvider armorModelProvider;
	@Unique
	@Environment(EnvType.CLIENT)
	private ArmorRenderingRegistry.TextureProvider armorTextureProvider;

	@Override
	@Environment(EnvType.CLIENT)
	public ArmorRenderingRegistry.ModelProvider fabric_getArmorModelProvider() {
		return armorModelProvider;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public ArmorRenderingRegistry.TextureProvider fabric_getArmorTextureProvider() {
		return armorTextureProvider;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fabric_setArmorModelProvider(ArmorRenderingRegistry.ModelProvider provider) {
		armorModelProvider = provider;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fabric_setArmorTextureProvider(ArmorRenderingRegistry.TextureProvider provider) {
		armorTextureProvider = provider;
	}
}