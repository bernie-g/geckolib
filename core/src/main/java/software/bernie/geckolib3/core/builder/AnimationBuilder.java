/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class follows the builder pattern, which means that every method returns an instance of this class. You can stack method calls, like this: <code>new AnimationBuilder().addAnimation("jump").addRepeatingAnimation("run", 5");</code>
 */
public class AnimationBuilder
{
	private List<RawAnimation> animationList = new ArrayList<>();

	/**
	 * Add a single animation to the queue and overrides the loop setting
	 *
	 * @param animationName The name of the animation. MUST MATCH THE NAME OF THE ANIMATION IN THE BLOCKBENCH FILE
	 * @param shouldLoop    loop
	 * @return An instance of the current animation builder
	 */
	public AnimationBuilder addAnimation(String animationName, Boolean shouldLoop)
	{
		animationList.add(new RawAnimation(animationName, shouldLoop));
		return this;
	}

	/**
	 * Add a single animation to the queue
	 *
	 * @param animationName The name of the animation. MUST MATCH THE NAME OF THE ANIMATION IN THE BLOCKBENCH FILE
	 * @return An instance of the current animation builder
	 */
	public AnimationBuilder addAnimation(String animationName)
	{
		animationList.add(new RawAnimation(animationName, null));
		return this;
	}

	/**
	 * Add multiple animations to the queue and overrides the loop setting to false
	 *
	 * @param animationName The name of the animation. MUST MATCH THE NAME OF THE ANIMATION IN THE BLOCKBENCH FILE
	 * @param timesToRepeat    How many times to add the animation to the queue
	 * @return An instance of the current animation builder
	 */
	public AnimationBuilder addRepeatingAnimation(String animationName, int timesToRepeat)
	{
		assert timesToRepeat > 0;
		for(int i = 0; i < timesToRepeat; i++)
		{
			addAnimation(animationName, false);
		}
		return this;
	}

	/**
	 * Clear all the animations in the animation builder.
	 *
	 * @return An instance of the current animation builder
	 */
	public AnimationBuilder clearAnimations()
	{
		animationList.clear();
		return this;
	}

	/**
	 * Gets the animations currently in this builder.
	 */
	public List<RawAnimation> getRawAnimationList()
	{
		return animationList;
	}

}
