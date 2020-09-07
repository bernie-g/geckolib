package software.bernie.geckolib.listener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.client.renderer.model.tile.JackInTheBoxModel;
import software.bernie.geckolib.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.geckolib.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.geckolib.example.client.renderer.tile.JackInTheBoxTileRenderer;
import software.bernie.geckolib.example.registry.BlockRegistry;
import software.bernie.geckolib.example.registry.ItemRegistry;
import software.bernie.geckolib.example.registry.TileRegistry;
import software.bernie.geckolib.item.AnimatedItemRenderer;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientListener
{
	public static final KeyBinding reloadGeckoLibKeyBind = new KeyBinding("Reloads GeckoLib models", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_R, "GeckoLib");

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.bindTileEntityRenderer(TileRegistry.JACK_IN_THE_BOX_TILE.get(), JackInTheBoxTileRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TileRegistry.BOTARIUM_TILE.get(), BotariumTileRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TileRegistry.FERTILIZER.get(), FertilizerTileRenderer::new);

		AnimatedItemRenderer renderer = (AnimatedItemRenderer) ItemRegistry.JACK_IN_THE_BOX_ITEM.get().getItemStackTileEntityRenderer();
		renderer.setModel(new JackInTheBoxModel());
		RenderTypeLookup.setRenderLayer(BlockRegistry.BOTARIUM_BLOCK.get(), RenderType.getCutout());

		if(!Arrays.stream(Minecraft.getInstance().gameSettings.keyBindings).anyMatch(x -> x.getKeyDescription().equals(reloadGeckoLibKeyBind.getKeyDescription())))
		{
			ClientRegistry.registerKeyBinding(reloadGeckoLibKeyBind);
		}
	}
}
