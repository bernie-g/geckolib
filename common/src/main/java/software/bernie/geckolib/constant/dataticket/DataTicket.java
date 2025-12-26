package software.bernie.geckolib.constant.dataticket;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.Objects;

/**
 * Ticket object to define a typed data object
 *
 * @param <D> Data type for this ticket
 */
public class DataTicket<D> {
	static final Map<Pair<Class<?>, String>, DataTicket<?>> IDENTITY_CACHE = new Object2ObjectOpenHashMap<>();

	private final String id;
	private final Class<? extends D> objectType;

	/**
	 * @see #create(String, Class)
	 */
	DataTicket(String id, Class<? extends D> objectType) {
		this.id = id;
		this.objectType = objectType;
	}

	/**
	 * Create a new DataTicket for a given name and object type
	 * <p>
	 * This DataTicket should then be stored statically somewhere and re-used.
	 */
	@SuppressWarnings("unchecked")
    public static <D> DataTicket<D> create(String id, Class<? extends D> objectType) {
		return (DataTicket<D>)IDENTITY_CACHE.computeIfAbsent(Pair.of(objectType, id), pair -> new DataTicket<>(id, objectType));
	}

	public String id() {
		return this.id;
	}

	public Class<? extends D> objectType() {
		return this.objectType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.objectType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof DataTicket<?> other))
			return false;

		return this.objectType == other.objectType && this.id.equals(other.id);
	}

	@Override
	public String toString() {
		return "DataTicket{" + this.id + ": " + this.objectType.getName() + "}";
	}
}
