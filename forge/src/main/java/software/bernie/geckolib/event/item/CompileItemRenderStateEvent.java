package software.bernie.geckolib.event.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Pre-render event for items being rendered by {@link GeoItemRenderer}
 * <p>
 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
 * <p>
 * Use this event to add data that you may need in a later {@link Item} event, or to override/replace data used in rendering
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public record CompileItemRenderStateEvent<T extends Item & GeoAnimatable>
        (GeoItemRenderer<T> renderer, GeoRenderState renderState, T animatable, GeoItemRenderer.RenderData renderData)
        implements GeoRenderEvent.Item.CompileRenderState<T>, RecordEvent {
    public static final EventBus<CompileItemRenderStateEvent> BUS = EventBus.create(CompileItemRenderStateEvent.class);

    @Override
    public GeoItemRenderer<T> getRenderer() {
        return this.renderer;
    }

    @Override
    public T getAnimatable() {
        return this.animatable;
    }

    @Override
    public GeoItemRenderer.RenderData getRenderData() {
        return this.renderData;
    }

    @ApiStatus.Internal
    @Override
    public GeoRenderState getRenderState() {
        return this.renderState;
    }
}
