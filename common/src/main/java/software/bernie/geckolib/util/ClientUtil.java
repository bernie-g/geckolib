package software.bernie.geckolib.util;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/**
 * Helper class for segregating client-side code
 */
public final class ClientUtil {
	/**
	 * Get the player on the client
	 */
	public static @Nullable Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	/**
	 * Gets the current level on the client
	 */
	public static @Nullable Level getLevel() {
		return Minecraft.getInstance().level;
	}

	/**
	 * Whether the local (client) player has a cape
	 */
	public static boolean clientPlayerHasCape() {
		final LocalPlayer player = Minecraft.getInstance().player;

		return player != null && player.getSkin().cape() != null;
	}

	/**
	 * Get the current camera position
	 */
	public static Vec3 getCameraPos() {
		return Minecraft.getInstance().gameRenderer.getMainCamera().position();
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
	 * Get the current phase of the moon on the client world
	 */
	public static MoonPhase getClientMoonPhase() {
		return Minecraft.getInstance().levelRenderer.levelRenderState.skyRenderState.moonPhase;
	}

    /**
     * Get the game time for the client world, or a global game time if no world is loaded<br>
     * Returned value is in ticks
     */
    public static double getCurrentTick() {
        final Minecraft mc = Minecraft.getInstance();

        return mc.level != null ?
               mc.level.getGameTime() + mc.getDeltaTracker().getGameTimeDeltaPartialTick(false) :
               Blaze3D.getTime() * 20d;
    }

    @ApiStatus.Internal
	public static int getVisibleEntityCount() {
        final Minecraft mc = Minecraft.getInstance();

        if (mc.level == null)
            return 0;

        return mc.levelRenderer.levelRenderState.entityRenderStates.size();
	}

    private ClientUtil() {}
}
