package software.bernie.geckolib3.core.event.predicate;

import java.util.List;
import java.util.stream.Collectors;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;

public class AnimationEvent<T extends IAnimatable> {
	private final T animatable;
	public double animationTick;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final boolean isMoving;
	private final List<Object> extraData;
	protected AnimationController controller;

	public AnimationEvent(T animatable, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving,
			List<Object> extraData) {
		this.animatable = animatable;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.isMoving = isMoving;
		this.extraData = extraData;
	}

	/**
	 * Gets the amount of ticks that have passed in either the current transition or
	 * animation, depending on the controller's AnimationState.
	 *
	 * @return the animation tick
	 */
	public double getAnimationTick() {
		return animationTick;
	}

	public T getAnimatable() {
		return animatable;
	}

	public float getLimbSwing() {
		return limbSwing;
	}

	public float getLimbSwingAmount() {
		return limbSwingAmount;
	}

	public float getPartialTick() {
		return partialTick;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public AnimationController getController() {
		return controller;
	}

	public void setController(AnimationController controller) {
		this.controller = controller;
	}

	public List<Object> getExtraData() {
		return extraData;
	}

	@SuppressWarnings("hiding")
	public <T> List<T> getExtraDataOfType(Class<T> type) {
		return extraData.stream().filter(x -> type.isAssignableFrom(x.getClass())).map(x -> type.cast(x))
				.collect(Collectors.toList());
	}
}
