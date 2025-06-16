/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.builder;

import java.util.ArrayList;
import java.util.List;

import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;

/**
 * This class follows the builder pattern, which means that every method returns
 * an instance of this class. You can stack method calls, like this:
 * <code>new AnimationBuilder().addAnimation("jump").addRepeatingAnimation("run", 5");</code>
 */
public class AnimationBuilder {
	private List<RawAnimation> animationList = new ArrayList<>();

	/**
	 * Add a single animation to the queue and overrides the loop setting
	 *
	 * @param animationName The name of the animation. MUST MATCH THE NAME OF THE
	 *                      ANIMATION IN THE BLOCKBENCH FILE
	 * @param shouldLoop    loop
	 * @return An instance of the current animation builder
	 */
	public AnimationBuilder addAnimation(String animationName, ILoopType loopType) {
		animationList.add(new RawAnimation(animationName, loopType));
		return this;
	}
	
	@Deprecated
	public AnimationBuilder addAnimation(String animationName, Boolean shouldLoop) {
		animationList.add(new RawAnimation(animationName, shouldLoop));
		return this;
	}

	/**
	 * Add a single animation to the queue
	 *
	 * @param animationName The name of the animation. MUST MATCH THE NAME OF THE
	 *                      ANIMATION IN THE BLOCKBENCH FILE
	 * @return An instance of the current animation builder
	 */
	public AnimationBuilder addAnimation(String animationName) {
		animationList.add(new RawAnimation(animationName, null));
		return this;
	}

	/**
	 * Add multiple animations to the queue and overrides the loop setting to false
	 *
	 * @param animationName The name of the animation. MUST MATCH THE NAME OF THE
	 *                      ANIMATION IN THE BLOCKBENCH FILE
	 * @param timesToRepeat How many times to add the animation to the queue
	 * @return An instance of the current animation builder
	 */
	public AnimationBuilder addRepeatingAnimation(String animationName, int timesToRepeat) {
		assert timesToRepeat > 0;
		for (int i = 0; i < timesToRepeat; i++) {
			addAnimation(animationName, EDefaultLoopTypes.PLAY_ONCE);
		}
		return this;
	}
	
	public AnimationBuilder playOnce(String animationName) {
		return this.addAnimation(animationName, EDefaultLoopTypes.PLAY_ONCE);
	}
	
	public AnimationBuilder loop(String animationName) {
		return this.addAnimation(animationName, EDefaultLoopTypes.LOOP);
	}
	
	/*
	 * Not implemented yet!
	 */
	public AnimationBuilder playAndHold(String animationName) {
		return this.addAnimation(animationName, EDefaultLoopTypes.HOLD_ON_LAST_FRAME);
	}
	
	//Below will use "Wait instructions", basically empty animations that do nothing, not sure if we really need those honestly
	public AnimationBuilder delayNext(int waitTimeTicks) {
		throw new UnsupportedOperationException("This isn't implemented yet, sorry!");
	}
	
	public AnimationBuilder playAndHoldFor(String animationName, int waitTimeTicks) {
		this.playAndHold(animationName);
		return this.delayNext(waitTimeTicks);
	}

	/**
	 * Clear all the animations in the animation builder.
	 *
	 * @return An instance of the current animation builder
	 */
	public AnimationBuilder clearAnimations() {
		animationList.clear();
		return this;
	}

	/**
	 * Gets the animations currently in this builder.
	 */
	public List<RawAnimation> getRawAnimationList() {
		return animationList;
	}

}
