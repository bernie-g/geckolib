package software.bernie.geckolib.constant.dataticket;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.Pair;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * {@link DataTicket} implementation for `DataTickets` that act as 'overrides' for values that already exist on a `RenderState`
 * <p>
 * This allows for using the existing/vanilla values by default, and the `DataTicket` value as an override only, maintaining compatibility
 * with vanilla and other mods that modify a `RenderState`'s values without using `DataTickets`
 * <p>
 * These can still be used as normal `DataTickets` if used in a context not relevant to their base class
 *
 * @param <D> Data type for this ticket
 * @param <C> The class type that this DataTicket overrides a value from
 */
public final class OverridingDataTicket<D, C> extends DataTicket<D> {
	private final Class<C> overriddenClass;
	private final Function<C, D> valueExtractor;

	private OverridingDataTicket(String id, Class<? extends D> objectType, Type dataType, Class<C> overriddenClass, Function<C, D> valueExtractor) {
		super(id, objectType, dataType);

		this.overriddenClass = overriddenClass;
		this.valueExtractor = valueExtractor;
	}

	/**
	 * Get the class type that this `DataTicket` overrides a value from
	 */
	public Class<C> getOverriddenClass() {
		return this.overriddenClass;
	}

	/**
	 * Get the value extractor function for this `DataTicket`
	 */
	public Function<C, D> getValueExtractor() {
		return this.valueExtractor;
	}

	/**
	 * Determine if this `DataTicket` can be extracted from the given `RenderState`
	 */
	public <R extends GeoRenderState> boolean canExtractFrom(R renderState) {
		return this.overriddenClass.isAssignableFrom(renderState.getClass());
	}

	/**
	 * Extract the base value from the given `RenderState` for this `DataTicket`
	 */
	public D extractFrom(C renderState) {
		return this.valueExtractor.apply(renderState);
	}

	/**
	 * Create a new value-overriding DataTicket
	 * <p>
	 * <b><u>MUST</u></b> be created during mod construct
	 * <p>
	 * This DataTicket should then be stored statically somewhere and re-used.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
    public static <D, C> OverridingDataTicket<D, C> create(String id, Class<? extends D> objectType, Class<C> overriddenClass, Function<C, D> valueExtractor) {
		return create(id, objectType, (TypeToken)TypeToken.of(objectType), overriddenClass, valueExtractor);
	}

	/**
	 * Create a new value-overriding DataTicket
	 * <p>
	 * <b><u>MUST</u></b> be created during mod construct
	 * <p>
	 * This DataTicket should then be stored statically somewhere and re-used.
	 */
	@SuppressWarnings("unchecked")
    public static <D, C> OverridingDataTicket<D, C> create(String id, Class<? extends D> objectType, TypeToken<D> typeToken, Class<C> overriddenClass, Function<C, D> valueExtractor) {
		return (OverridingDataTicket<D, C>)IDENTITY_CACHE.computeIfAbsent(Pair.of(objectType, id), pair ->
				new OverridingDataTicket<>(id, objectType, typeToken.getType(), overriddenClass, valueExtractor));
	}
}
