package software.bernie.geckolib3.core.object;

import java.util.Map;
import java.util.Objects;

/**
 * Ticket object to define a typed data object
 * @param id The name of the data value
 * @param objectType The class type of the data
 */
public record DataTicket<D>(String id, Class<? extends D> objectType) {
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.objectType);
	}

	/**
	 * Reverse getter function for consistent operation of ticket data retrieval
	 * @param dataMap The data map to retrieve the data from
	 */
	public <D> D getData(Map<DataTicket<?>, ?> dataMap) {
		return (D)dataMap.get(this);
	}
}
