package software.bernie.geckolib.animation;

import java.util.HashMap;

public class AnimationControllerCollection extends HashMap<String, AnimationController>
{
	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for the key, the old
	 * value is replaced.
	 *
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or
	 * <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 * (A <tt>null</tt> return can also indicate that the map
	 * previously associated <tt>null</tt> with <tt>key</tt>.)
	 */
	public AnimationController addAnimationController(AnimationController value)
	{
		return this.put(value.name, value);
	}

	public boolean hasRegisteredControllers = false;

}
