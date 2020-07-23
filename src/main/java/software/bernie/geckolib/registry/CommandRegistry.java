package software.bernie.geckolib.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.command.ReloadAnimationsCommand;

public class CommandRegistry
{
	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> commands = dispatcher.register(
				CommandManager.literal(GeckoLib.ModID).then(ReloadAnimationsCommand.register(dispatcher))

		);
		dispatcher.register(CommandManager.literal("gl").redirect(commands));
	}
}
