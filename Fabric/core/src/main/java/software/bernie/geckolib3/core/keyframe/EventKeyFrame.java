/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

public class EventKeyFrame<T> {
	private T eventData;
	private Double startTick;

	public EventKeyFrame(Double startTick, T eventData) {
		this.startTick = startTick;
		this.eventData = eventData;
	}

	public T getEventData() {
		return eventData;
	}

	public Double getStartTick() {
		return startTick;
	}
}
