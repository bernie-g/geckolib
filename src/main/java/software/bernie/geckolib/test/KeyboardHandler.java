package software.bernie.geckolib.test;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyboardHandler
{
	public static boolean isForwardKeyDown = false;
	public static boolean isBackKeyDown = false;

	@SubscribeEvent
	public static void onKeyPress(final InputEvent.KeyInputEvent event)
	{
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null)
		{
			isForwardKeyDown = mc.gameSettings.keyBindForward.isKeyDown();
			isBackKeyDown = mc.gameSettings.keyBindBack.isKeyDown();

		}
	}
}
