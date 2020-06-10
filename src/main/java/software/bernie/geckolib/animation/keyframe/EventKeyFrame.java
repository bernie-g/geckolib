/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.keyframe;

public class EventKeyFrame
{
	private String eventData;
	public boolean hasExecuted = false;
	private Double startTick;

	public EventKeyFrame(Double startTick, String eventData)
	{
		this.startTick = startTick;
		this.eventData = eventData;
	}

	public String getEventData()
	{
		return eventData;
	}

	public Double getStartTick()
	{
		return startTick;
	}
}
