package software.bernie.geckolib.constant.dataticket;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.constant.DataTickets;

/**
 * Network-compatible {@link DataTicket} implementation
 * <p>
 * Used for sending data from server -> client in an easy manner
 *
 * @param <D> Data type for this ticket
 */
public final class SerializableDataTicket<D> extends DataTicket<D> {
	public static final StreamCodec<RegistryFriendlyByteBuf, SerializableDataTicket<?>> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			SerializableDataTicket::id,
            DataTickets::byName);

	private final StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec;

	private SerializableDataTicket(String id, Class<? extends D> objectType, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		super(id, objectType);

		this.streamCodec = streamCodec;
	}
	/**
	 * Create a new network-syncable DataTicket for a given name and object type
	 * <p>
	 * <b><u>MUST</u></b> be created during mod construct
	 * <p>
	 * This DataTicket should then be stored statically somewhere and re-used.
	 */
	public static <D> SerializableDataTicket<D> create(String id, Class<? extends D> objectType, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		return (SerializableDataTicket<D>)IDENTITY_CACHE.computeIfAbsent(Pair.of(objectType, id), pair -> DataTickets.registerSerializable(new SerializableDataTicket<>(id, objectType, streamCodec)));
	}

	/**
	 * @return The {@link StreamCodec} for the given SerializableDataTicket
	 */
	public StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec() {
		return this.streamCodec;
	}

	// Pre-defined typings for use

	/**
	 * Generate a new {@code SerializableDataTicket<Double>} for the given id
	 *
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Double> ofDouble(Identifier id) {
		return SerializableDataTicket.create(id.toString(), Double.class, ByteBufCodecs.DOUBLE);
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Float>} for the given id
	 *
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Float> ofFloat(Identifier id) {
		return SerializableDataTicket.create(id.toString(), Float.class, ByteBufCodecs.FLOAT);
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Boolean>} for the given id
	 *
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Boolean> ofBoolean(Identifier id) {
		return SerializableDataTicket.create(id.toString(), Boolean.class, ByteBufCodecs.BOOL);
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Integer>} for the given id
	 *
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Integer> ofInt(Identifier id) {
		return SerializableDataTicket.create(id.toString(), Integer.class, ByteBufCodecs.INT);
	}

	/**
	 * Generate a new {@code SerializableDataTicket<String>} for the given id
	 *
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<String> ofString(Identifier id) {
		return SerializableDataTicket.create(id.toString(), String.class, ByteBufCodecs.STRING_UTF8);
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Enum>} for the given id
	 *
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static <E extends Enum<E>> SerializableDataTicket<E> ofEnum(Identifier id, Class<E> enumClass) {
		return SerializableDataTicket.create(id.toString(), enumClass, new StreamCodec<>() {
			@Override
			public E decode(RegistryFriendlyByteBuf buf) {
				return Enum.valueOf(enumClass, buf.readUtf());
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buf, E data) {
				buf.writeUtf(data.toString());
			}
		});
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Vec3>} for the given id
	 *
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Vec3> ofVec3(Identifier id) {
		return SerializableDataTicket.create(id.toString(), Vec3.class, ByteBufCodecs.VECTOR3F.map(Vec3::new, Vec3::toVector3f));
	}

	/**
	 * Generate a new {@code SerializableDataTicket<BlockPos>} for the given id
	 *
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<BlockPos> ofBlockPos(Identifier id) {
		return SerializableDataTicket.create(id.toString(), BlockPos.class, new StreamCodec<>() {
			@Override
			public BlockPos decode(RegistryFriendlyByteBuf buf) {
				return buf.readBlockPos();
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buf, BlockPos blockPos) {
				buf.writeBlockPos(blockPos);
			}
		});
	}
}
