/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.core.animation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Objects;

/**
 * A builder class for a raw/unbaked animation. These are constructed to pass to the
 * {@link AnimationController} to build into full-fledged animations for usage.
 * <br><br>
 * Animations added to this builder are added <u>in order of insertion</u> - the animations will play in the order that you define them.<br>
 * RawAnimation instances should be cached statically where possible to reduce overheads and improve efficiency.
 * <br><br>
 * Example usage: <br>
 * <pre>{@code RawAnimation.begin().thenPlay("action.open_box").thenLoop("state.stay_open")}</pre>
 */
public final class RawAnimation {
	private final List<Stage> animationList = new ObjectArrayList<>();

	// Private constructor to force usage of factory for logical operations
	private RawAnimation() {}

	/**
	 * Start a new RawAnimation instance. This is the start point for creating an animation chain.
	 * @return A new RawAnimation instance
	 */
	public static RawAnimation begin() {
		return new RawAnimation();
	}

	/**
	 * Append an animation to the animation chain, playing the named animation and stopping
	 * or progressing to the next chained animation depending on the loop type set in the animation json
	 * @param animationName The name of the animation to play once
	 */
	public RawAnimation thenPlay(String animationName) {
		return then(animationName, Animation.LoopType.DEFAULT);
	}

	/**
	 * Append an animation to the animation chain, playing the named animation and repeating it continuously until the animation is stopped by external sources.
	 * @param animationName The name of the animation to play on a loop
	 */
	public RawAnimation thenLoop(String animationName) {
		return then(animationName, Animation.LoopType.LOOP);
	}

	/**
	 * Appends a 'wait' animation to the animation chain.<br>
	 * This causes the animatable to do nothing for a set period of time before performing the next animation.
	 * @param ticks The number of ticks to 'wait' for
	 */
	public RawAnimation thenWait(int ticks) {
		this.animationList.add(new Stage(Stage.WAIT, Animation.LoopType.PLAY_ONCE, ticks));

		return this;
	}

	/**
	 * Appends an animation to the animation chain, then has the animatable hold the pose at the end of the
	 * animation until it is stopped by external sources.
	 * @param animation The name of the animation to play and hold
	 */
	public RawAnimation thenPlayAndHold(String animation) {
		return then(animation, Animation.LoopType.HOLD_ON_LAST_FRAME);
	}

	/**
	 * Append an animation to the animation chain, playing the named animation <code>playCount</code> times,
	 * then stopping or progressing to the next chained animation depending on the loop type set in the animation json
	 * @param animationName The name of the animation to play X times
	 * @param playCount The number of times to repeat the animation before proceeding
	 */
	public RawAnimation thenPlayXTimes(String animationName, int playCount) {
		for (int i = 0; i < playCount; i++) {
			then(animationName, i == playCount - 1 ? Animation.LoopType.DEFAULT : Animation.LoopType.PLAY_ONCE);
		}

		return this;
	}

	/**
	 * Append an animation to the animation chain, playing the named animation and proceeding based on the <code>loopType</code> parameter provided.
	 * @param animationName The name of the animation to play. <u>MUST</u> match the name of the animation in the <code>.animation.json</code> file.
	 * @param loopType The loop type handler for the animation, overriding the default value set in the animation json
	 */
	public RawAnimation then(String animationName, Animation.LoopType loopType) {
		this.animationList.add(new Stage(animationName, loopType));

		return this;
	}

	public List<Stage> getAnimationStages() {
		return this.animationList;
	}

	/**
	 * Create a new RawAnimation instance based on an existing RawAnimation instance.
	 * The new instance will be a shallow copy of the other instance, and can then be appended to or otherwise modified
	 * @param other The existing RawAnimation instance to copy
	 * @return A new instance of RawAnimation
	 */
	public static RawAnimation copyOf(RawAnimation other) {
		RawAnimation newInstance = RawAnimation.begin();

		newInstance.animationList.addAll(other.animationList);

		return newInstance;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		return hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.animationList);
	}

	/**
	 * An animation stage for a {@link RawAnimation} builder.<br>
	 * This is an entry object representing a single animation stage of the final compiled animation.
	 */
	public record Stage(String animationName, Animation.LoopType loopType, int additionalTicks) {
		static final String WAIT = "internal.wait";

		public Stage(String animationName, Animation.LoopType loopType) {
			this(animationName, loopType, 0);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;

			if (obj == null || getClass() != obj.getClass())
				return false;

			return hashCode() == obj.hashCode();
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.animationName, this.loopType);
		}
	}
}
