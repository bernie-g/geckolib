/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.keyframe.event;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.keyframe.event.data.CustomInstructionKeyframeData;

/**
 * The {@link KeyFrameEvent} specific to the {@link AnimationController#customKeyframeHandler}
 * <p>
 * Called when a custom instruction keyframe is encountered
 */
public class CustomInstructionKeyframeEvent<T extends GeoAnimatable> extends KeyFrameEvent<T, CustomInstructionKeyframeData> {
	public CustomInstructionKeyframeEvent(T entity, double animationTick, AnimationController<T> controller,
										  CustomInstructionKeyframeData customInstructionKeyframeData, AnimationState<T> animationState) {
		super(entity, animationTick, controller, customInstructionKeyframeData, animationState);
	}

	/**
	 * Get the {@link CustomInstructionKeyframeData} relevant to this event call
	 */
	@Override
	public CustomInstructionKeyframeData getKeyframeData() {
		return super.getKeyframeData();
	}
}
