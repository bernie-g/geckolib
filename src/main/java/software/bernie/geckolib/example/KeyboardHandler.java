/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class KeyboardHandler
{
	public static boolean isForwardKeyDown = false;
	public static boolean isBackKeyDown = false;
	public static boolean isQDown = false;

	@SubscribeEvent
	public static void onKeyPress(final InputEvent.KeyInputEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player != null)
		{
			isForwardKeyDown = mc.gameSettings.keyBindForward.isKeyDown();
			isBackKeyDown = mc.gameSettings.keyBindBack.isKeyDown();
			isQDown = mc.gameSettings.keyBindDrop.isKeyDown();
		}
	}
}
