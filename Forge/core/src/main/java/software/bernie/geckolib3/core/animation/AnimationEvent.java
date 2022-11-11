package software.bernie.geckolib3.core.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.object.DataTicket;

import java.util.Map;

/**
 * Animation state handler for end-users.<br>
 * This is where users would set their selected animation to play,
 * stop the controller, or any number of other animation-related actions.
 */
public class AnimationEvent<T extends GeoAnimatable> {
	private final T animatable;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final boolean isMoving;
	private final Map<DataTicket<?>, Object> extraData = new Object2ObjectOpenHashMap<>();

	protected AnimationController<T> controller;
	public double animationTick;

	public AnimationEvent(T animatable, float partialTick) {
		this(animatable, 0, 0, partialTick, false);
	}

	public AnimationEvent(T animatable, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving) {
		this.animatable = animatable;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.isMoving = isMoving;
	}

	/**
	 * Gets the amount of ticks that have passed in either the current transition or
	 * animation, depending on the controller's AnimationState.
	 */
	public double getAnimationTick() {
		return this.animationTick;
	}

	/**
	 * Gets the current {@link GeoAnimatable} being rendered
	 */
	public T getAnimatable() {
		return this.animatable;
	}

	public float getLimbSwing() {
		return this.limbSwing;
	}

	public float getLimbSwingAmount() {
		return this.limbSwingAmount;
	}

	/**
	 * Gets the fractional value of the current game tick that has passed in rendering
	 */
	public float getPartialTick() {
		return this.partialTick;
	}

	/**
	 * Gets whether the current {@link GeoAnimatable} is considered to be moving for animation purposes.<br>
	 * Note that this is a best-case approximation of movement, and your needs may vary.
	 */
	public boolean isMoving() {
		return this.isMoving;
	}

	/**
	 * Gets the current {@link AnimationController} responsible for the current animation
	 */
	public AnimationController<T> getController() {
		return this.controller;
	}

	/**
	 * Sets the {@code AnimationEvent}'s current {@link AnimationController}
	 */
	public AnimationEvent<T> withController(AnimationController<T> controller) {
		this.controller = controller;

		return this;
	}

	/**
	 * Gets the optional additional data map for the event.<br>
	 * @see DataTicket
	 */
	public Map<DataTicket<?>, ?> getExtraData() {
		return this.extraData;
	}

	/**
	 * Get a data value saved to this animation event by the ticket for that data.<br>
	 * @see DataTicket
	 * @param dataTicket The {@link DataTicket} for the data to retrieve
	 * @return The cached data for the given {@code DataTicket}, or null if not saved
	 */
	public <D> D getData(DataTicket<D> dataTicket) {
		return dataTicket.getData(this.extraData);
	}

	/**
	 * Save a data value for the given {@link DataTicket} in the additional data map
	 * @param dataTicket The {@code DataTicket} for the data value
	 * @param data The data value
	 */
	public <D> void setData(DataTicket<D> dataTicket, D data) {
		this.extraData.put(dataTicket, data);
	}
}
