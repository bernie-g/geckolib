package software.bernie.geckolib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Helper class for segregating client-side code
 */
public final class ClientUtils {
	/**
	 * Get the player on the client
	 */
	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	/**
	 * Gets the current level on the client
	 */
	public static Level getLevel() {
		return Minecraft.getInstance().level;
	}
}
