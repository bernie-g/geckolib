package software.bernie.geckolib3q.network;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import software.bernie.geckolib3q.GeckoLib;

public class ClientPackets implements ClientModInitializer {

	public static void registerClientPackets() {
		// Server --> Client
		ClientPlayNetworking.registerGlobalReceiver(GeckoLibNetwork.SYNCABLE, ClientPackets::handleSyncPacket);
	}

	public static void handleSyncPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf,
			PacketSender responseSender) {

		final String key = buf.readUtf();
		final int id = buf.readVarInt();
		final int state = buf.readVarInt();

		final ISyncable syncable = GeckoLibNetwork.getSyncable(key);
		if (syncable != null) {
			syncable.onAnimationSync(id, state);
		} else {
			GeckoLib.LOGGER.warn("Syncable on the server is missing on the client for " + key);
		}
	}

	@Override
	public void onInitializeClient(ModContainer mod) {
		registerClientPackets();
	}

}
