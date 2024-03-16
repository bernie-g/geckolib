package software.bernie.geckolib.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.network.packet.*;

/**
 * Network handling class for GeckoLib.<br>
 * Handles packet registration and some networking functions
 */
public final class GeckoLibNetwork {
	public static void init(IEventBus modBus) {
		modBus.addListener(GeckoLibNetwork::registerPackets);
	}

	private static void registerPackets(final RegisterPayloadHandlerEvent ev) {
		final IPayloadRegistrar registrar = ev.registrar(GeckoLibConstants.MODID);

		registrar.play(AnimDataSyncPacket.ID, AnimDataSyncPacket::decode, AnimDataSyncPacket::receivePacket);
		registrar.play(AnimTriggerPacket.ID, AnimTriggerPacket::decode, AnimTriggerPacket::receivePacket);
		registrar.play(EntityAnimDataSyncPacket.ID, EntityAnimDataSyncPacket::decode, EntityAnimDataSyncPacket::receivePacket);
		registrar.play(EntityAnimTriggerPacket.ID, EntityAnimTriggerPacket::decode, EntityAnimTriggerPacket::receivePacket);
		registrar.play(BlockEntityAnimDataSyncPacket.ID, BlockEntityAnimDataSyncPacket::decode, BlockEntityAnimDataSyncPacket::receivePacket);
		registrar.play(BlockEntityAnimTriggerPacket.ID, BlockEntityAnimTriggerPacket::decode, BlockEntityAnimTriggerPacket::receivePacket);
	}

	/**
	 * Send a packet using GeckoLib's packet channel
	 */
	public static <M> void send(M packet, PacketDistributor.PacketTarget distributor) {
		distributor.send((CustomPacketPayload)packet);
	}
}
