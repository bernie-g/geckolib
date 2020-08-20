package software.bernie.geckolib.listener;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.client.renderer.tile.JackInTheBoxTileRenderer;
import software.bernie.geckolib.example.registry.BlockRegistry;
import software.bernie.geckolib.example.registry.TileRegistry;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientListener
{
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.bindTileEntityRenderer(TileRegistry.JACK_IN_THE_BOX_TILE.get(), JackInTheBoxTileRenderer::new);

		RenderTypeLookup.setRenderLayer(BlockRegistry.JACK_IN_THE_BOX.get(), RenderType.getCutout());
	}
}
