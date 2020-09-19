/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

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
	public static boolean isQDown = false;

	@SubscribeEvent
	public static void onKeyPress(final InputEvent.KeyInputEvent event)
	{
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null)
		{
			isForwardKeyDown = mc.gameSettings.keyBindForward.isKeyDown();
			isBackKeyDown = mc.gameSettings.keyBindBack.isKeyDown();
			isQDown = mc.gameSettings.keyBindDrop.isKeyDown();
		}
	}
}
