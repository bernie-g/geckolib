package software.bernie.geckolib3.network;

import java.util.Map;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;

public class GeckoLibNetwork {
	private static final Map<String, Supplier<ISyncable>> SYNCABLES = new Object2ObjectOpenHashMap<>();
	public static final Identifier SYNCABLE = new Identifier(GeckoLib.ModID, "syncable");

	public static void syncAnimation(PlayerEntity target, ISyncable syncable, int id, int state) {
		if (target.world.isClient) {
			throw new IllegalArgumentException("Only the server can request animation syncs!");
		}
		final String key = syncable.getSyncKey();
		if (!SYNCABLES.containsKey(key)) {
			throw new IllegalArgumentException("Syncable not registered for " + key);
		}

		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		encodeSyncPacket(buf, key, id, state);

		ServerPlayNetworking.send((ServerPlayerEntity) target, SYNCABLE, buf);
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

	public static void encodeSyncPacket(PacketByteBuf buf, String key, int id, int state) {
		buf.writeString(key);
		buf.writeVarInt(id);
		buf.writeVarInt(state);
	}
}
