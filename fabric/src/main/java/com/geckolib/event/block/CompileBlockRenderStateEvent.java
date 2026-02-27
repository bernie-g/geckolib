package com.geckolib.event.block;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;

/// Pre-render event for blocks being rendered by [GeoBlockRenderer]
///
/// This event is called in preparation for rendering, when the renderer is gathering data to pass through
///
/// Use this event to add data that you may need in a later [Block] event, or to override/replace data used in rendering
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @param <T> BlockEntity animatable class type
/// @param <R> RenderState class type
/// @see GeoRenderEvent
/// @see CompileRenderState
public class CompileBlockRenderStateEvent<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> implements GeoRenderEvent.Block.CompileRenderState<T, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoBlockRenderer<T, R> renderer;
    private final R renderState;
    private final T animatable;

    public CompileBlockRenderStateEvent(GeoBlockRenderer<T, R> renderer, R renderState, T animatable) {
        this.renderer = renderer;
        this.renderState = renderState;
        this.animatable = animatable;
    }

    @Override
    public GeoBlockRenderer<T, R> getRenderer() {
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

    /// Event listener interface for the [Block.CompileRenderState] GeoRenderEvent
    ///
    /// @param <T> BlockEntity animatable class type
    /// @param <R> RenderState class type
    @FunctionalInterface
    public interface Listener<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> {
        void handle(CompileBlockRenderStateEvent<T, R> event);
    }
}
