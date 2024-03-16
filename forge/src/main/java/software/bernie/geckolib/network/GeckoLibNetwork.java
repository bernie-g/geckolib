package software.bernie.geckolib.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.network.packet.*;

/**
 * Network handling class for GeckoLib.<br>
 * Handles packet registration and some networking functions
 */
public final class GeckoLibNetwork {
	private static final SimpleChannel PACKET_CHANNEL = ChannelBuilder.named(new ResourceLocation(GeckoLibConstants.MODID, "main")).simpleChannel();
	public static void init() {
		PACKET_CHANNEL.messageBuilder(AnimDataSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(AnimDataSyncPacket::encode).decoder(AnimDataSyncPacket::decode).consumerMainThread(AnimDataSyncPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(AnimTriggerPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(AnimTriggerPacket::encode).decoder(AnimTriggerPacket::decode).consumerMainThread(AnimTriggerPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(EntityAnimDataSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(EntityAnimDataSyncPacket::encode).decoder(EntityAnimDataSyncPacket::decode).consumerMainThread(EntityAnimDataSyncPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(EntityAnimTriggerPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(EntityAnimTriggerPacket::encode).decoder(EntityAnimTriggerPacket::decode).consumerMainThread(EntityAnimTriggerPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(BlockEntityAnimDataSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(BlockEntityAnimDataSyncPacket::encode).decoder(BlockEntityAnimDataSyncPacket::decode).consumerMainThread(BlockEntityAnimDataSyncPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(BlockEntityAnimTriggerPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(BlockEntityAnimTriggerPacket::encode).decoder(BlockEntityAnimTriggerPacket::decode).consumerMainThread(BlockEntityAnimTriggerPacket::receivePacket).add();
	}

	/**
	 * Send a packet using GeckoLib's packet channel
	 */
	public static <M> void send(M packet, PacketDistributor.PacketTarget distributor) {
		PACKET_CHANNEL.send(packet, distributor);
	}
}
