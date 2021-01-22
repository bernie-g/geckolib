/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.builder;

import java.util.Objects;

public class RawAnimation
{
	public String animationName;

	/**
	 * If loop is null, the animation processor will use the loopByDefault boolean to decide if the animation should loop.
	 */
	public Boolean loop;

	/**
	 * A raw animation only stores the animation name and if it should loop, nothing else
	 *
	 * @param animationName The name of the animation
	 * @param loop          Whether it should loop
	 */
	public RawAnimation(String animationName, Boolean loop)
	{
		this.animationName = animationName;
		this.loop = loop;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		if(!(obj instanceof RawAnimation))
		{
			return false;
		}
		RawAnimation animation = (RawAnimation) obj;
		if(animation.loop == this.loop && animation.animationName.equals(this.animationName))
		{
			return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(animationName, loop);
	}
}
