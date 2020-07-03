/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = EnvType.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyboardHandler
{
	public static boolean isForwardKeyDown = false;
	public static boolean isBackKeyDown = false;
	public static boolean isQDown = false;

	@SubscribeEvent
	public static void onKeyPress(final InputEvent.KeyInputEvent event)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player != null)
		{
			isForwardKeyDown = mc.options.keyForward.isPressed();
			isBackKeyDown = mc.options.keyBack.isPressed();
			isQDown = mc.options.keyDrop.isPressed();

		}
	}
}
