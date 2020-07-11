/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.snapshot;

import software.bernie.geckolib.animation.render.AnimatedModelRenderer;

//idk what else to call this lol
public class DirtyTracker
{
	public AnimatedModelRenderer model;
	public boolean hasScaleChanged;
	public boolean hasPositionChanged;
	public boolean hasRotationChanged;

	public DirtyTracker(boolean hasScaleChanged, boolean hasPositionChanged, boolean hasRotationChanged, AnimatedModelRenderer model)
	{
		this.hasScaleChanged = hasScaleChanged;
		this.hasPositionChanged = hasPositionChanged;
		this.hasRotationChanged = hasRotationChanged;
		this.model = model;
	}
}
