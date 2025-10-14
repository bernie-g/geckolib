package software.bernie.geckolib.event.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * One-time event for a {@link GeoEntityRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public record CompileEntityRenderLayersEvent<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
        (GeoEntityRenderer<T, R> renderer)
    implements GeoRenderEvent.Entity.CompileRenderLayers<T, R>, RecordEvent {
    public static final EventBus<CompileEntityRenderLayersEvent> BUS = EventBus.create(CompileEntityRenderLayersEvent.class);

    @Override
    public GeoEntityRenderer<T, R> getRenderer() {
        return this.renderer;
    }
}
