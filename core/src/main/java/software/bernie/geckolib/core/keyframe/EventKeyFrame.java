/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.core.keyframe;

public class EventKeyFrame<T> {
    public boolean hasExecuted = false;
    private final T eventData;
    private final Double startTick;

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
