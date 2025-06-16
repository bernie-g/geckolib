/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */
package anightdazingzoroark.riftlib.core;

import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.core.manager.AnimationFactory;

/**
 * This interface must be applied to any object that wants to be animated
 */
public interface IAnimatable {
	void registerControllers(AnimationData data);

	AnimationFactory getFactory();
}
