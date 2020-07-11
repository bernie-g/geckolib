package software.bernie.geckolib.listener;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.registry.CommandRegistry;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerListener
{
	@SubscribeEvent
	public static void onServerStartingEvent(FMLServerStartingEvent event)
	{
		CommandRegistry.registerCommands(event.getCommandDispatcher());
	}
}
