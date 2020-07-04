package software.bernie.geckolib.listener;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import software.bernie.geckolib.registry.CommandRegistry;

public class ServerListener
{
	// We don't have events like this in Fabric so this gets called by our ModInitializer
	public static void onServerStartingEvent(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated)
	{
		CommandRegistry.registerCommands(dispatcher);
	}
}
