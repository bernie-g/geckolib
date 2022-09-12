/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.snapshot;

import software.bernie.geckolib3.core.processor.IBone;

public class DirtyTracker {
	public IBone model;
	public boolean hasScaleChanged;
	public boolean hasPositionChanged;
	public boolean hasRotationChanged;

	public DirtyTracker(boolean hasScaleChanged, boolean hasPositionChanged, boolean hasRotationChanged, IBone model) {
		this.hasScaleChanged = hasScaleChanged;
		this.hasPositionChanged = hasPositionChanged;
		this.hasRotationChanged = hasRotationChanged;
		this.model = model;
	}
}
