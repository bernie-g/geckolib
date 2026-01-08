package software.bernie.geckolib.constant.dataticket;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Ticket object to define a typed data object
 *
 * @param <D> Data type for this ticket
 */
public class DataTicket<D> {
	static final Map<Pair<Type, String>, DataTicket<?>> IDENTITY_CACHE = new Object2ObjectOpenHashMap<>();

	private final String id;
	private final Class<? extends D> objectType;
	private final Type dataType;

	/**
	 * @see #create(String, Class)
	 */
	DataTicket(String id, Class<? extends D> objectType, Type dataType) {
		this.id = id;
		this.objectType = objectType;
		this.dataType = dataType;
	}

	/**
	 * Create a new DataTicket for a given name and object type
	 * <p>
	 * This DataTicket should then be stored statically somewhere and re-used.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
    public static <D> DataTicket<D> create(String id, Class<? extends D> objectType) {
		return create(id, objectType, (TypeToken)TypeToken.of(objectType));
	}

	/**
	 * Create a new DataTicket for a given name and object type
	 * <p>
	 * This DataTicket should then be stored statically somewhere and re-used.
	 */
	@SuppressWarnings("unchecked")
    public static <D> DataTicket<D> create(String id, Class<? super D> objectType, TypeToken<D> token) {
		return (DataTicket<D>)IDENTITY_CACHE.computeIfAbsent(Pair.of(token.getType(), id), pair -> new DataTicket<>(id, objectType, token.getType()));
	}

	public String id() {
		return this.id;
	}

	/**
	 * Get the object class that this DataTicket's data is for
	 *
	 * @deprecated Use {@link #dataType()} instead
	 */
	@Deprecated(forRemoval = true)
	public Class<? extends D> objectType() {
		return this.objectType;
	}

	/**
	 * Get the java object type that this DataTicket's data is for
	 */
	public Type dataType() {
		return this.dataType;
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
