package software.bernie.geckolib.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.reload.ReloadManager;

public class ReloadAnimationsCommand implements Command<ServerCommandSource>
{
	private static final ReloadAnimationsCommand CMD = new ReloadAnimationsCommand();

	public static LiteralArgumentBuilder<ServerCommandSource> register(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		return CommandManager.literal("reload")
				.requires(cs -> cs.hasPermissionLevel(0))
				.executes(CMD);
	}


	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		ReloadableResourceManager resourceManager = (ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager();
		for(SynchronousResourceReloadListener model : ReloadManager.getRegisteredModels())
		{
			model.apply(resourceManager);
		}
		for(AnimationController controller : ReloadManager.getRegisteredAnimationControllers())
		{
			controller.markNeedsReload();
		}

		Style style = new Style();
		style.setColor(Formatting.GREEN);
		context.getSource().sendFeedback(new LiteralText("Reloaded " + ReloadManager.getRegisteredModels().size() + " animations.").setStyle(
				style), false);
		return 0;
	}


}
