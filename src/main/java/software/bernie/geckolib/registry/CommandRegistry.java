package software.bernie.geckolib.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.command.ReloadAnimationsCommand;

public class CommandRegistry
{
	public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
		LiteralCommandNode<CommandSource> commands = dispatcher.register(
				Commands.literal(GeckoLib.ModID).then(ReloadAnimationsCommand.register(dispatcher))

		);
		dispatcher.register(Commands.literal("gl").redirect(commands));
	}
}
