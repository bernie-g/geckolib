package software.bernie.geckolib.network;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
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
	private static final SimpleChannel PACKET_CHANNEL = ChannelBuilder.named(new ResourceLocation(GeckoLib.MOD_ID, "main")).simpleChannel();
	private static final Map<String, GeoAnimatable> SYNCED_ANIMATABLES = new Object2ObjectOpenHashMap<>();

	public static void init() {
		PACKET_CHANNEL.messageBuilder(AnimDataSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(AnimDataSyncPacket::encode).decoder(AnimDataSyncPacket::decode).consumerMainThread(AnimDataSyncPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(AnimTriggerPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(AnimTriggerPacket::encode).decoder(AnimTriggerPacket::decode).consumerMainThread(AnimTriggerPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(EntityAnimDataSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(EntityAnimDataSyncPacket::encode).decoder(EntityAnimDataSyncPacket::decode).consumerMainThread(EntityAnimDataSyncPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(EntityAnimTriggerPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(EntityAnimTriggerPacket::encode).decoder(EntityAnimTriggerPacket::decode).consumerMainThread(EntityAnimTriggerPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(BlockEntityAnimDataSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(BlockEntityAnimDataSyncPacket::encode).decoder(BlockEntityAnimDataSyncPacket::decode).consumerMainThread(BlockEntityAnimDataSyncPacket::receivePacket).add();
		PACKET_CHANNEL.messageBuilder(BlockEntityAnimTriggerPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(BlockEntityAnimTriggerPacket::encode).decoder(BlockEntityAnimTriggerPacket::decode).consumerMainThread(BlockEntityAnimTriggerPacket::receivePacket).add();
	}

	/**
	 * Registers a synced {@link GeoAnimatable} object for networking support.<br>
	 * It is recommended that you don't call this directly, instead implementing and calling {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable#registerSyncedAnimatable}
	 */
	synchronized public static void registerSyncedAnimatable(GeoAnimatable animatable) {
		GeoAnimatable existing = SYNCED_ANIMATABLES.put(animatable.getClass().toString(), animatable);

		if (existing == null)
			GeckoLib.LOGGER.debug("Registered SyncedAnimatable for " + animatable.getClass().toString());
	}

	/**
	 * Gets a registered synced {@link GeoAnimatable} object by name
	 * @param className
	 */
	@Nullable
	public static GeoAnimatable getSyncedAnimatable(String className) {
		GeoAnimatable animatable = SYNCED_ANIMATABLES.get(className);

		if (animatable == null)
			GeckoLib.LOGGER.error("Attempting to retrieve unregistered synced animatable! (" + className + ")");

		return animatable;
	}

	/**
	 * Send a packet using GeckoLib's packet channel
	 */
	public static <M> void send(M packet, PacketDistributor.PacketTarget distributor) {
		PACKET_CHANNEL.send(packet, distributor);
	}
}
