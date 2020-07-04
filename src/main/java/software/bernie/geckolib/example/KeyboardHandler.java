/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class KeyboardHandler
{
	public static boolean isSneakKeyDown()  {
		return MinecraftClient.getInstance().options.keySneak.isPressed();
	}

	public static boolean isForwardKeyDown()  {
		return MinecraftClient.getInstance().options.keyForward.isPressed();
	}

	public static boolean isBackKeyDown() {
		return MinecraftClient.getInstance().options.keyBack.isPressed();
	}
}
