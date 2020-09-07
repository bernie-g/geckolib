/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.snapshot;

import software.bernie.geckolib.animation.processor.IBone;

import java.util.ArrayList;

public class EntityDirtyTracker extends ArrayList<DirtyTracker>
{
	public DirtyTracker get(IBone bone)
	{
		return this.stream().filter(x -> x.model.getName().equals(bone.getName())).findFirst().orElse(null);
	}

}
