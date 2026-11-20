package software.bernie.geckolib.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.packet.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

/**
 * Network handling class for GeckoLib.<br>
 * Handles packet registration and some networking functions
 */
public final class GeckoLibNetwork {
	private static final String VER = "2";
	private static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(GeckoLib.MOD_ID, "main"), () -> VER, clientVer -> true, VER::equals);

	private static final Int2ObjectMap<String> ANIMATABLE_IDENTITIES = new Int2ObjectOpenHashMap<>();
	private static final Map<String, GeoAnimatable> SYNCED_ANIMATABLES = new Object2ObjectOpenHashMap<>();

	public static void init() {
		int id = 0;

		PACKET_CHANNEL.registerMessage(id++, AnimDataSyncPacket.class, AnimDataSyncPacket::encode, AnimDataSyncPacket::decode, AnimDataSyncPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		PACKET_CHANNEL.registerMessage(id++, AnimTriggerPacket.class, AnimTriggerPacket::encode, AnimTriggerPacket::decode, AnimTriggerPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		PACKET_CHANNEL.registerMessage(id++, StopTriggeredSingletonAnimPacket.class, StopTriggeredSingletonAnimPacket::encode, StopTriggeredSingletonAnimPacket::decode, StopTriggeredSingletonAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		PACKET_CHANNEL.registerMessage(id++, EntityAnimDataSyncPacket.class, EntityAnimDataSyncPacket::encode, EntityAnimDataSyncPacket::decode, EntityAnimDataSyncPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		PACKET_CHANNEL.registerMessage(id++, EntityAnimTriggerPacket.class, EntityAnimTriggerPacket::encode, EntityAnimTriggerPacket::decode, EntityAnimTriggerPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		PACKET_CHANNEL.registerMessage(id++, StopTriggeredEntityAnimPacket.class, StopTriggeredEntityAnimPacket::encode, StopTriggeredEntityAnimPacket::decode, StopTriggeredEntityAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		PACKET_CHANNEL.registerMessage(id++, BlockEntityAnimDataSyncPacket.class, BlockEntityAnimDataSyncPacket::encode, BlockEntityAnimDataSyncPacket::decode, BlockEntityAnimDataSyncPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		PACKET_CHANNEL.registerMessage(id++, BlockEntityAnimTriggerPacket.class, BlockEntityAnimTriggerPacket::encode, BlockEntityAnimTriggerPacket::decode, BlockEntityAnimTriggerPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		PACKET_CHANNEL.registerMessage(id++, StopTriggeredBlockEntityAnimPacket.class, StopTriggeredBlockEntityAnimPacket::encode, StopTriggeredBlockEntityAnimPacket::decode, StopTriggeredBlockEntityAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        PACKET_CHANNEL.registerMessage(id++, StatelessSingletonPlayAnimPacket.class, StatelessSingletonPlayAnimPacket::encode, StatelessSingletonPlayAnimPacket::decode, StatelessSingletonPlayAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PACKET_CHANNEL.registerMessage(id++, StatelessSingletonStopAnimPacket.class, StatelessSingletonStopAnimPacket::encode, StatelessSingletonStopAnimPacket::decode, StatelessSingletonStopAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PACKET_CHANNEL.registerMessage(id++, StatelessEntityPlayAnimPacket.class, StatelessEntityPlayAnimPacket::encode, StatelessEntityPlayAnimPacket::decode, StatelessEntityPlayAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PACKET_CHANNEL.registerMessage(id++, StatelessEntityStopAnimPacket.class, StatelessEntityStopAnimPacket::encode, StatelessEntityStopAnimPacket::decode, StatelessEntityStopAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PACKET_CHANNEL.registerMessage(id++, StatelessBlockEntityPlayAnimPacket.class, StatelessBlockEntityPlayAnimPacket::encode, StatelessBlockEntityPlayAnimPacket::decode, StatelessBlockEntityPlayAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PACKET_CHANNEL.registerMessage(id++, StatelessBlockEntityStopAnimPacket.class, StatelessBlockEntityStopAnimPacket::encode, StatelessBlockEntityStopAnimPacket::decode, StatelessBlockEntityStopAnimPacket::receivePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	/**
	 * Registers a synced {@link GeoAnimatable} object for networking support.<br>
	 * It is recommended that you don't call this directly, instead implementing and calling {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable#registerSyncedAnimatable}
	 */
	synchronized public static void registerSyncedAnimatable(GeoAnimatable animatable) {
		GeoAnimatable existing = SYNCED_ANIMATABLES.put(getSyncedSingletonAnimatableId(animatable), animatable);

		if (existing == null)
			GeckoLib.LOGGER.debug("Registered SyncedAnimatable for " + animatable.getClass());
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
