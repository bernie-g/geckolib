/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.builder;

import java.util.Objects;

import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;

public class RawAnimation {
	public String animationName;

	/**
	 * If loop is null, the animation processor will use the loopByDefault boolean
	 * to decide if the animation should loop.
	 */
	public ILoopType loopType;

	/**
	 * A raw animation only stores the animation name and if it should loop, nothing
	 * else
	 *
	 * @param animationName The name of the animation
	 * @param loop          Whether it should loop
	 */
	public RawAnimation(String animationName, ILoopType loop) {
		this.animationName = animationName;
		this.loopType = loop;
	}

	/**
	 * Use {@link ILoopType} constructor
	 */
	@Deprecated(forRemoval = true)
	public RawAnimation(String animationName, boolean loop) {
		this(animationName, loop ? EDefaultLoopTypes.LOOP : EDefaultLoopTypes.PLAY_ONCE);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof RawAnimation animation)) {
			return false;
		}		
		return animation.loopType == this.loopType && animation.animationName.equals(this.animationName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.animationName, this.loopType);
	}
}
