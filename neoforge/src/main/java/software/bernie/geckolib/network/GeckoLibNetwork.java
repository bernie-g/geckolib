package software.bernie.geckolib.network;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.packet.*;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Network handling class for GeckoLib.<br>
 * Handles packet registration and some networking functions
 */
public final class GeckoLibNetwork {
	public static void init(IEventBus modBus) {
		modBus.addListener(GeckoLibNetwork::registerPackets);
	}

	private static void registerPackets(final RegisterPayloadHandlerEvent ev) {
		final IPayloadRegistrar registrar = ev.registrar(GeckoLib.MOD_ID);

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
