package software.bernie.geckolib.animatable.processing;

import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Animation state handler for end-users
 * <p>
 * This is where users would set their selected animation to play,
 * stop the controller, or any number of other animation-related actions.
 */
public record AnimationState<T extends GeoAnimatable>(GeoRenderState renderState, AnimatableManager<T> manager, float partialTick, Reference2DoubleMap<Variable> queryValues, AnimationController<T> controller) {
	public AnimationState(GeoRenderState renderState) {
		this(renderState,
			 renderState.getGeckolibData(DataTickets.ANIMATABLE_MANAGER),
			 renderState.getPartialTick(),
			 renderState.getOrDefaultGeckolibData(DataTickets.QUERY_VALUES, new Reference2DoubleOpenHashMap<>(0)),
			 null);
	}

	/**
	 * Get a data value saved to the {@link GeoRenderState} by the ticket for that data.
	 * <p>
	 * Note that you should <b><u>NOT</u></b> be attempting to retrieve data you don't know exists.<br>
	 * Use {@link #hasData(DataTicket)} if unsure
	 *
	 * @param dataTicket The {@link DataTicket} for the data to retrieve
	 * @return The cached data for the given {@code DataTicket}, which can be null
	 */
	@Nullable
	public <D> D getData(DataTicket<D> dataTicket) {
		return this.renderState.getGeckolibData(dataTicket);
	}

	/**
	 * Get a data value saved to the {@link GeoRenderState} by the ticket for that data
	 * <p>
	 * Note that you should <b><u>NOT</u></b> be attempting to retrieve data you don't know exists.<br>
	 * Use {@link #hasData(DataTicket)} if unsure
	 *
	 * @param dataTicket The {@link DataTicket} for the data to retrieve
	 * @param fallback The fallback value to use if the data hasn't been stored
	 * @return The cached data for the given {@code DataTicket}, or null if not saved
	 */
	public <D> D getDataOrDefault(DataTicket<D> dataTicket, D fallback) {
		return this.renderState.getOrDefaultGeckolibData(dataTicket, fallback);
	}

	/**
	 * Check if a given {@link DataTicket} has been set for this {@link GeoRenderState}
	 *
	 * @param dataTicket The {@link DataTicket} for the data to check
	 */
	public <D> boolean hasData(DataTicket<D> dataTicket) {
		return this.renderState.hasGeckolibData(dataTicket);
	}

	/**
	 * Save a data value for the given {@link DataTicket} in the additional data map
	 *
	 * @param dataTicket The {@code DataTicket} for the data value
	 * @param data The data value
	 */
	public <D> void setData(DataTicket<D> dataTicket, D data) {
		this.renderState.addGeckolibData(dataTicket, data);
	}

	/**
	 * Returns a new AnimationState matching this one but with the new AnimationController
	 */
	public AnimationState<T> withController(AnimationController<T> controller) {
		return new AnimationState<>(this.renderState, this.manager, this.partialTick, this.queryValues, controller);
	}

	/**
	 * Get the pre-computed Molang query value for the provided {@link Variable}
	 * <p>
	 * Only variables relevant to the animations on this AnimationState, this render frame, will be present
	 */
	@ApiStatus.Internal
	public double getActorVariableValue(Variable variable) {
		return this.queryValues.getDouble(variable);
	}
}
