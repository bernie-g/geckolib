package software.bernie.geckolib.listener;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.client.renderer.model.tile.JackInTheBoxModel;
import software.bernie.geckolib.example.client.renderer.tile.JackInTheBoxTileRenderer;
import software.bernie.geckolib.example.registry.BlockRegistry;
import software.bernie.geckolib.example.registry.ItemRegistry;
import software.bernie.geckolib.example.registry.TileRegistry;
import software.bernie.geckolib.itemstack.AnimatedItemRenderer;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientListener
{
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.bindTileEntityRenderer(TileRegistry.JACK_IN_THE_BOX_TILE.get(), JackInTheBoxTileRenderer::new);
		AnimatedItemRenderer renderer = (AnimatedItemRenderer) ItemRegistry.JACK_IN_THE_BOX_ITEM.get().getItemStackTileEntityRenderer();
		renderer.setModel(new JackInTheBoxModel());
		RenderTypeLookup.setRenderLayer(BlockRegistry.JACK_IN_THE_BOX.get(), RenderType.getCutout());


	}
}
