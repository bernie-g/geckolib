package software.bernie.geckolib3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ArmorProviderExtensions {
    /*  */
    @Environment(EnvType.CLIENT)
    ArmorRenderingRegistry.ModelProvider fabric_getArmorModelProvider();

    /*  */
    @Environment(EnvType.CLIENT)
    ArmorRenderingRegistry.TextureProvider fabric_getArmorTextureProvider();

    @Environment(EnvType.CLIENT)
    void fabric_setArmorModelProvider(/*  */ ArmorRenderingRegistry.ModelProvider provider);

    @Environment(EnvType.CLIENT)
    void fabric_setArmorTextureProvider(/*  */ ArmorRenderingRegistry.TextureProvider provider);
}