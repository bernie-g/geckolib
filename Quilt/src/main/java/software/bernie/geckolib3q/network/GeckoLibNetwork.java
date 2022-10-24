package software.bernie.geckolib3q.network;

import java.util.Map;
import java.util.function.Supplier;

import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib3q.GeckoLib;

public class GeckoLibNetwork {
	private static final Map<String, Supplier<ISyncable>> SYNCABLES = new Object2ObjectOpenHashMap<>();
	public static final ResourceLocation SYNCABLE = new ResourceLocation(GeckoLib.ModID, "syncable");

	public static void syncAnimation(Player target, ISyncable syncable, int id, int state) {
		if (target.level.isClientSide()) {
			throw new IllegalArgumentException("Only the server can request animation syncs!");
		}
		final String key = syncable.getSyncKey();
		if (!SYNCABLES.containsKey(key)) {
			throw new IllegalArgumentException("Syncable not registered for " + key);
		}

		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

		encodeSyncPacket(buf, key, id, state);

		ServerPlayNetworking.send((ServerPlayer) target, SYNCABLE, buf);
	}

	public static ISyncable getSyncable(String key) {
		final Supplier<ISyncable> delegate = SYNCABLES.get(key);
		return delegate == null ? null : delegate.get();
	}

	// there is no forge registry entries
	public static void registerSyncable(ISyncable entry) {
		final String key = entry.getSyncKey();
		if (SYNCABLES.putIfAbsent(key, () -> entry) != null) {
			throw new IllegalArgumentException("Syncable already registered for " + key);
		}
		GeckoLib.LOGGER.debug("Registered syncable for " + key);
	}

	public static void encodeSyncPacket(FriendlyByteBuf buf, String key, int id, int state) {
		buf.writeUtf(key);
		buf.writeVarInt(id);
		buf.writeVarInt(state);
	}
}
