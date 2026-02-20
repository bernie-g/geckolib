package software.bernie.geckolib.constant.dataticket;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.constant.DataTickets;

import java.lang.reflect.Type;

/// Network-compatible [DataTicket] implementation
///
/// Used for sending data from `server -> client` in an easy manner
///
/// @param <D> Data type for this ticket
public final class SerializableDataTicket<D> extends DataTicket<D> {
	public static final StreamCodec<RegistryFriendlyByteBuf, SerializableDataTicket<?>> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			SerializableDataTicket::getRegisteredId,
            SerializableDataTicket::enforceValidTicket);

	private final StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec;
	private final Identifier registeredId;

	private SerializableDataTicket(Identifier id, Type dataType, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		super(id.toString(), dataType);

		this.streamCodec = streamCodec;
		this.registeredId = id;
	}

	/// Get the registered ID for this ticket
	public Identifier getRegisteredId() {
		return this.registeredId;
	}

	/// Create a new network-syncable DataTicket for a given name and object type
	///
	/// **<u>MUST</u>** be created during mod construct
	///
	/// This DataTicket should then be stored statically somewhere and re-used.
	@SuppressWarnings({"unchecked", "rawtypes"})
    public static <D> SerializableDataTicket<D> create(Identifier id, Class<? extends D> objectType, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		return create(id, (TypeToken)TypeToken.of(objectType), streamCodec);
	}

	/// Create a new network-syncable DataTicket for a given name and object type
	///
	/// **<u>MUST</u>** be created during mod construct
	///
	/// This DataTicket should then be stored statically somewhere and re-used.
	@SuppressWarnings("unchecked")
    public static <D> SerializableDataTicket<D> create(Identifier id, TypeToken<D> typeToken, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		return (SerializableDataTicket<D>)IDENTITY_CACHE.computeIfAbsent(Pair.of(typeToken.getType(), id.toString()), pair ->
				DataTickets.registerSerializable(new SerializableDataTicket<>(id, typeToken.getType(), streamCodec)));
	}

	/// @return The [StreamCodec] for the given SerializableDataTicket
	public StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec() {
		return this.streamCodec;
	}

	// Pre-defined common types for use

	/// Generate a new `SerializableDataTicket<Double>` for the given id
	///
	/// @param id The unique id of your ticket. Include your modid
	public static SerializableDataTicket<Double> ofDouble(Identifier id) {
		return SerializableDataTicket.create(id, Double.class, ByteBufCodecs.DOUBLE);
	}

	/// Generate a new `SerializableDataTicket<Float>` for the given id
	///
	/// @param id The unique id of your ticket. Include your modid
	public static SerializableDataTicket<Float> ofFloat(Identifier id) {
		return SerializableDataTicket.create(id, Float.class, ByteBufCodecs.FLOAT);
	}

	/// Generate a new `SerializableDataTicket<Boolean>` for the given id
	///
	/// @param id The unique id of your ticket. Include your modid
	public static SerializableDataTicket<Boolean> ofBoolean(Identifier id) {
		return SerializableDataTicket.create(id, Boolean.class, ByteBufCodecs.BOOL);
	}

	/// Generate a new `SerializableDataTicket<Integer>` for the given id
	///
	/// @param id The unique id of your ticket. Include your modid
	public static SerializableDataTicket<Integer> ofInt(Identifier id) {
		return SerializableDataTicket.create(id, Integer.class, ByteBufCodecs.INT);
	}

	/// Generate a new `SerializableDataTicket<String>` for the given id
	///
	/// @param id The unique id of your ticket. Include your modid
	public static SerializableDataTicket<String> ofString(Identifier id) {
		return SerializableDataTicket.create(id, String.class, ByteBufCodecs.STRING_UTF8);
	}

	/// Generate a new `SerializableDataTicket<Enum>` for the given id
	///
	/// @param id The unique id of your ticket. Include your modid
	public static <E extends Enum<E>> SerializableDataTicket<E> ofEnum(Identifier id, Class<E> enumClass) {
		return SerializableDataTicket.create(id, enumClass, new StreamCodec<>() {
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

	/// Generate a new `SerializableDataTicket<Vec3>` for the given id
	///
	/// @param id The unique id of your ticket. Include your modid
	public static SerializableDataTicket<Vec3> ofVec3(Identifier id) {
		return SerializableDataTicket.create(id, Vec3.class, ByteBufCodecs.VECTOR3F.map(Vec3::new, Vec3::toVector3f));
	}

	/// Generate a new `SerializableDataTicket<BlockPos>` for the given id
	///
	/// @param id The unique id of your ticket. Include your modid
	public static SerializableDataTicket<BlockPos> ofBlockPos(Identifier id) {
		return SerializableDataTicket.create(id, BlockPos.class, new StreamCodec<>() {
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

	/// Retrieve a SerializableDataTicket by its registered ID, throwing an exception if not found
	public static SerializableDataTicket<?> enforceValidTicket(Identifier name) throws IllegalStateException {
		final SerializableDataTicket<?> ticket = DataTickets.byName(name);

		if (ticket == null)
			throw new IllegalStateException("Attempted to retrieve a SerializableDataTicket that does not exist! Likely didn't register the ticket properly: " + name);

		return ticket;
	}
}
