package software.bernie.geckolib.core.object;

import java.util.Map;
import java.util.Objects;

/**
 * Ticket object to define a typed data object
 */
public class DataTicket<D> {
	private final String id;
	private final Class<? extends D> objectType;

	public DataTicket(String id, Class<? extends D> objectType) {
		this.id = id;
		this.objectType = objectType;
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

	/**
	 * Reverse getter function for consistent operation of ticket data retrieval
	 * @param dataMap The data map to retrieve the data from
	 * @return The data from the map, or null if the data hasn't been stored
	 */
	public <D> D getData(Map<? extends DataTicket<?>, ?> dataMap) {
		return (D)dataMap.get(this);
	}
}
