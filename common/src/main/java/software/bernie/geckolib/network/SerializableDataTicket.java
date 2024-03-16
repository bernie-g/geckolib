package software.bernie.geckolib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.object.DataTicket;

/**
 * Network-compatible {@link software.bernie.geckolib.core.object.DataTicket} implementation.
 * Used for sending data from server -> client in an easy manner
 */
public abstract class SerializableDataTicket<D> extends DataTicket<D> {
	public SerializableDataTicket(String id, Class<? extends D> objectType) {
		super(id, objectType);
	}

	/**
	 * Encode the object to a packet buffer for transmission
	 * @param data The object to be serialized
	 * @param buffer The buffer to serialize the object to
	 */
	public abstract void encode(D data, FriendlyByteBuf buffer);

	/**
	 * Decode the object from a packet buffer after transmission
	 * @param buffer The buffer to deserialize the object from
	 * @return A new instance of your data object
	 */
	public abstract D decode(FriendlyByteBuf buffer);

	// Pre-defined typings for use

	/**
	 * Generate a new {@code SerializableDataTicket<Double>} for the given id
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Double> ofDouble(ResourceLocation id) {
		return new SerializableDataTicket<>(id.toString(), Double.class) {
			@Override
			public void encode(Double data, FriendlyByteBuf buffer) {
				buffer.writeDouble(data);
			}

			@Override
			public Double decode(FriendlyByteBuf buffer) {
				return buffer.readDouble();
			}
		};
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Float>} for the given id
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Float> ofFloat(ResourceLocation id) {
		return new SerializableDataTicket<>(id.toString(), Float.class) {
			@Override
			public void encode(Float data, FriendlyByteBuf buffer) {
				buffer.writeFloat(data);
			}

			@Override
			public Float decode(FriendlyByteBuf buffer) {
				return buffer.readFloat();
			}
		};
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Boolean>} for the given id
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Boolean> ofBoolean(ResourceLocation id) {
		return new SerializableDataTicket<>(id.toString(), Boolean.class) {
			@Override
			public void encode(Boolean data, FriendlyByteBuf buffer) {
				buffer.writeBoolean(data);
			}

			@Override
			public Boolean decode(FriendlyByteBuf buffer) {
				return buffer.readBoolean();
			}
		};
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Integer>} for the given id
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<Integer> ofInt(ResourceLocation id) {
		return new SerializableDataTicket<>(id.toString(), Integer.class) {
			@Override
			public void encode(Integer data, FriendlyByteBuf buffer) {
				buffer.writeVarInt(data);
			}

			@Override
			public Integer decode(FriendlyByteBuf buffer) {
				return buffer.readVarInt();
			}
		};
	}

	/**
	 * Generate a new {@code SerializableDataTicket<String>} for the given id
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static SerializableDataTicket<String> ofString(ResourceLocation id) {
		return new SerializableDataTicket<>(id.toString(), String.class) {
			@Override
			public void encode(String data, FriendlyByteBuf buffer) {
				buffer.writeUtf(data);
			}

			@Override
			public String decode(FriendlyByteBuf buffer) {
				return buffer.readUtf();
			}
		};
	}

	/**
	 * Generate a new {@code SerializableDataTicket<Enum>} for the given id
	 * @param id The unique id of your ticket. Include your modid
	 */
	public static <E extends Enum<E>> SerializableDataTicket<E> ofEnum(ResourceLocation id, Class<E> enumClass) {
		return new SerializableDataTicket<>(id.toString(), enumClass) {
			@Override
			public void encode(E data, FriendlyByteBuf buffer) {
				buffer.writeUtf(data.toString());
			}

			@Override
			public E decode(FriendlyByteBuf buffer) {
				return Enum.valueOf(enumClass, buffer.readUtf());
			}
		};
	}
}
