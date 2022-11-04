/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.state;

import software.bernie.geckolib3.core.animatable.model.GeoBone;

public class DirtyTracker {
	public final GeoBone bone;
	public boolean hasScaleChanged;
	public boolean hasPositionChanged;
	public boolean hasRotationChanged;

	public DirtyTracker(GeoBone bone, boolean hasScaleChanged, boolean hasPositionChanged, boolean hasRotationChanged) {
		this.hasScaleChanged = hasScaleChanged;
		this.hasPositionChanged = hasPositionChanged;
		this.hasRotationChanged = hasRotationChanged;
		this.bone = bone;
	}
}
