package software.bernie.geckolib3.network;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import software.bernie.geckolib3.GeckoLib;

public class ClientPackets implements ClientModInitializer {

	public static void registerClientPackets() {
		// Server --> Client
		ClientPlayNetworking.registerGlobalReceiver(GeckoLibNetwork.SYNCABLE, ClientPackets::handleSyncPacket);
	}

	public static void handleSyncPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
			PacketSender responseSender) {

		final String key = buf.readString();
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
	public void onInitializeClient() {
		registerClientPackets();
	}

}
