package com.geckolib.event.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

/**
 * Pre-render event for entities being rendered by {@link GeoEntityRenderer}
 * <p>
 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
 * <p>
 * Use this event to add data that you may need in a later {@link Entity} event, or to override/replace data used in rendering
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public record CompileEntityRenderStateEvent<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
        (GeoEntityRenderer<T, R> renderer, R renderState, T animatable)
    implements GeoRenderEvent.Entity.CompileRenderState<T, R>, RecordEvent {
    public static final EventBus<CompileEntityRenderStateEvent> BUS = EventBus.create(CompileEntityRenderStateEvent.class);

    @Override
    public GeoEntityRenderer<T, R> getRenderer() {
        return this.renderer;
    }

    @Override
    public T getAnimatable() {
        return this.animatable;
    }

    @ApiStatus.Internal
    @Override
    public R getRenderState() {
        return this.renderState;
    }
}
