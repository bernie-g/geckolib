package software.bernie.geckolib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Helper class for segregating client-side code
 */
public final class ClientUtil {
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

	/**
	 * Whether the local (client) player has a cape
	 */
	public static boolean clientPlayerHasCape() {
		return Minecraft.getInstance().player.getSkin().capeTexture() != null;
	}

	/**
	 * Get the current camera position
	 */
	public static Vec3 getCameraPos() {
		return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
	}

	/**
	 * Helper method to check for first-person camera mode
	 * <p>
	 * Split off to preserve side-agnosticism of the Molang system
	 */
	public static boolean isFirstPerson() {
		return Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}

	public static int getVisibleEntityCount() {
		return Minecraft.getInstance().levelRenderer.visibleEntityCount;
	}
}
