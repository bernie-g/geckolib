package software.bernie.geckolib;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ArmorProviderExtensions {
    /* @Nullable */
    @Environment(EnvType.CLIENT)
    ArmorRenderingRegistry.ModelProvider fabric_getArmorModelProvider();

    /* @Nullable */
    @Environment(EnvType.CLIENT)
    ArmorRenderingRegistry.TextureProvider fabric_getArmorTextureProvider();

    @Environment(EnvType.CLIENT)
    void fabric_setArmorModelProvider(/* @Nullable */ ArmorRenderingRegistry.ModelProvider provider);

    @Environment(EnvType.CLIENT)
    void fabric_setArmorTextureProvider(/* @Nullable */ ArmorRenderingRegistry.TextureProvider provider);
}