package software.bernie.geckolib.constant.dataticket;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/// Ticket object to define a typed data object
///
/// @param <D> Data type for this ticket
public class DataTicket<D> {
	static final Map<Pair<Type, String>, DataTicket<?>> IDENTITY_CACHE = new Object2ObjectOpenHashMap<>();

	private final String id;
	private final Type dataType;

	/// @see #create(String, Class)
	DataTicket(String id, Type dataType) {
		this.id = id;
		this.dataType = dataType;
	}

	/// Create a new DataTicket for a given name and object type
	///
	/// This DataTicket should then be stored statically somewhere and re-used.
	@SuppressWarnings({"unchecked", "rawtypes"})
    public static <D> DataTicket<D> create(String id, Class<? extends D> objectType) {
		return create(id, (TypeToken)TypeToken.of(objectType));
	}

	/// Create a new DataTicket for a given name and object type
	///
	/// This DataTicket should then be stored statically somewhere and re-used.
	@SuppressWarnings("unchecked")
    public static <D> DataTicket<D> create(String id, TypeToken<D> token) {
		return (DataTicket<D>)IDENTITY_CACHE.computeIfAbsent(Pair.of(token.getType(), id), pair -> new DataTicket<>(id, token.getType()));
	}

	public String id() {
		return this.id;
	}

	/// Get the java object type that this DataTicket's data is for
	public Type dataType() {
		return this.dataType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.dataType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof DataTicket<?> other))
			return false;

		return this.dataType == other.dataType && this.id.equals(other.id);
	}

	@Override
	public String toString() {
		return "DataTicket{" + this.id + ": " + this.dataType.getTypeName() + "}";
	}
}
