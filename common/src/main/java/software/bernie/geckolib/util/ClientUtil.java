package software.bernie.geckolib.util;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

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
		return Minecraft.getInstance().player.getSkin().cape() != null;
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

    /**
     * Returns the current time (in ticks) that the {@link org.lwjgl.glfw.GLFW GLFW} instance has been running
     * <p>
     * This is effectively a permanent timer that counts up since the game was launched.
     */
    public static double getCurrentTick() {
        return Blaze3D.getTime() * 20d;
    }

    @ApiStatus.Internal
	public static int getVisibleEntityCount() {
        final Minecraft mc = Minecraft.getInstance();

        if (mc.level == null)
            return 0;

        return mc.levelRenderer.levelRenderState.entityRenderStates.size();
	}
}
