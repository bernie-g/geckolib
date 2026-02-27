package com.geckolib.constant.dataticket;

import com.geckolib.renderer.base.GeoRenderState;
import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.Pair;

import java.lang.reflect.Type;
import java.util.function.Function;

/// [DataTicket] implementation for `DataTickets` that act as 'overrides' for values that already exist on a `RenderState`
///
/// This allows for using the existing/vanilla values by default, and the `DataTicket` value as an override only, maintaining compatibility
/// with vanilla and other mods that modify a `RenderState`'s values without using `DataTickets`
///
/// These can still be used as normal `DataTickets` if used in a context not relevant to their base class
///
/// @param <D> Data type for this ticket
/// @param <C> The class type that this DataTicket overrides a value from
public final class OverridingDataTicket<D, C> extends DataTicket<D> {
	private final Class<C> overriddenClass;
	private final Function<C, D> valueExtractor;

	private OverridingDataTicket(String id, Type dataType, Class<C> overriddenClass, Function<C, D> valueExtractor) {
		super(id, dataType);

		this.overriddenClass = overriddenClass;
		this.valueExtractor = valueExtractor;
	}

	/// Get the class type that this `DataTicket` overrides a value from
	public Class<C> getOverriddenClass() {
		return this.overriddenClass;
	}

	/// Get the value extractor function for this `DataTicket`
	public Function<C, D> getValueExtractor() {
		return this.valueExtractor;
	}

	/// Determine if this `DataTicket` can be extracted from the given `RenderState`
	public <R extends GeoRenderState> boolean canExtractFrom(R renderState) {
		return this.overriddenClass.isAssignableFrom(renderState.getClass());
	}

	/// Extract the base value from the given `RenderState` for this `DataTicket`
	public D extractFrom(C renderState) {
		return this.valueExtractor.apply(renderState);
	}

	/// Create a new value-overriding DataTicket
	///
	/// **<u>MUST</u>** be created during mod construct
	///
	/// This DataTicket should then be stored statically somewhere and re-used.
	@SuppressWarnings({"unchecked", "rawtypes"})
    public static <D, C> OverridingDataTicket<D, C> create(String id, Class<? extends D> objectType, Class<C> overriddenClass, Function<C, D> valueExtractor) {
		return create(id, (TypeToken)TypeToken.of(objectType), overriddenClass, valueExtractor);
	}

	/// Create a new value-overriding DataTicket
	///
	/// **<u>MUST</u>** be created during mod construct
	///
	/// This DataTicket should then be stored statically somewhere and re-used.
	@SuppressWarnings("unchecked")
    public static <D, C> OverridingDataTicket<D, C> create(String id, TypeToken<D> typeToken, Class<C> overriddenClass, Function<C, D> valueExtractor) {
		return (OverridingDataTicket<D, C>)IDENTITY_CACHE.computeIfAbsent(Pair.of(typeToken.getType(), id), _ ->
				new OverridingDataTicket<>(id, typeToken.getType(), overriddenClass, valueExtractor));
	}
}