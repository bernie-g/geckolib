package software.bernie.geckolib.network;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
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
	private static final String VER = "1";
	private static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(GeckoLib.MOD_ID, "main"), () -> VER, VER::equals, VER::equals);

	private static final Int2ObjectMap<String> ANIMATABLE_IDENTITIES = new Int2ObjectOpenHashMap<>();
	private static final Map<String, GeoAnimatable> SYNCED_ANIMATABLES = new Object2ObjectOpenHashMap<>();

	public static void init() {
		int id = 0;

		PACKET_CHANNEL.registerMessage(id++, AnimDataSyncPacket.class, AnimDataSyncPacket::encode, AnimDataSyncPacket::decode, AnimDataSyncPacket::receivePacket);
		PACKET_CHANNEL.registerMessage(id++, AnimTriggerPacket.class, AnimTriggerPacket::encode, AnimTriggerPacket::decode, AnimTriggerPacket::receivePacket);
		PACKET_CHANNEL.registerMessage(id++, StopTriggeredSingletonAnimPacket.class, StopTriggeredSingletonAnimPacket::encode, StopTriggeredSingletonAnimPacket::decode, StopTriggeredSingletonAnimPacket::receivePacket);
		PACKET_CHANNEL.registerMessage(id++, EntityAnimDataSyncPacket.class, EntityAnimDataSyncPacket::encode, EntityAnimDataSyncPacket::decode, EntityAnimDataSyncPacket::receivePacket);
		PACKET_CHANNEL.registerMessage(id++, EntityAnimTriggerPacket.class, EntityAnimTriggerPacket::encode, EntityAnimTriggerPacket::decode, EntityAnimTriggerPacket::receivePacket);
		PACKET_CHANNEL.registerMessage(id++, StopTriggeredEntityAnimPacket.class, StopTriggeredEntityAnimPacket::encode, StopTriggeredEntityAnimPacket::decode, StopTriggeredEntityAnimPacket::receivePacket);
		PACKET_CHANNEL.registerMessage(id++, BlockEntityAnimDataSyncPacket.class, BlockEntityAnimDataSyncPacket::encode, BlockEntityAnimDataSyncPacket::decode, BlockEntityAnimDataSyncPacket::receivePacket);
		PACKET_CHANNEL.registerMessage(id++, BlockEntityAnimTriggerPacket.class, BlockEntityAnimTriggerPacket::encode, BlockEntityAnimTriggerPacket::decode, BlockEntityAnimTriggerPacket::receivePacket);
		PACKET_CHANNEL.registerMessage(id++, StopTriggeredBlockEntityAnimPacket.class, StopTriggeredBlockEntityAnimPacket::encode, StopTriggeredBlockEntityAnimPacket::decode, StopTriggeredBlockEntityAnimPacket::receivePacket);
	}

	/**
	 * Registers a synced {@link GeoAnimatable} object for networking support.<br>
	 * It is recommended that you don't call this directly, instead implementing and calling {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable#registerSyncedAnimatable}
	 */
	synchronized public static void registerSyncedAnimatable(GeoAnimatable animatable) {
		GeoAnimatable existing = SYNCED_ANIMATABLES.put(getSyncedSingletonAnimatableId(animatable), animatable);

		if (existing == null)
			GeckoLib.LOGGER.debug("Registered SyncedAnimatable for " + animatable.getClass().toString());
	}

	/**
	 * Gets a registered synced {@link GeoAnimatable} object by name
	 * <p>
	 * The input string <b>MUST</b> be the {@link Class#getName()} of the animatable, passed through
	 * {@link #getSyncedSingletonAnimatableId(GeoAnimatable)}
	 *
	 * @param syncedAnimatableId the animatable id
	 */
	@Nullable
	public static GeoAnimatable getSyncedAnimatable(String syncedAnimatableId) {
		GeoAnimatable animatable = SYNCED_ANIMATABLES.get(syncedAnimatableId);

		if (animatable == null)
			GeckoLib.LOGGER.error("Attempting to retrieve unregistered synced animatable! (" + syncedAnimatableId + ")");

		return animatable;
	}

	/**
	 * Send a packet using GeckoLib's packet channel
	 */
	public static <M> void send(M packet, PacketDistributor.PacketTarget distributor) {
		PACKET_CHANNEL.send(distributor, packet);
	}

	/**
	 * Get a synced singleton animatable's id for use with {@link #SYNCED_ANIMATABLES}
	 * <p>
	 * This <b><u>MUST</u></b> be used when retrieving from {@link #SYNCED_ANIMATABLES}
	 * as this method eliminates class duplication collisions
	 */
	public static String getSyncedSingletonAnimatableId(GeoAnimatable animatable) {
		return ANIMATABLE_IDENTITIES.computeIfAbsent(System.identityHashCode(animatable), i -> {
			String baseId = animatable.getClass().getName();
			i = 0;

			while (SYNCED_ANIMATABLES.containsKey(baseId + i)) {
				i++;
			}

			return baseId + i;
		});
	}
}
