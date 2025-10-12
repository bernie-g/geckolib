package software.bernie.geckolib.event.replacedentity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * One-time event for a {@link GeoReplacedEntityRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public record CompileReplacedEntityRenderLayersEvent<T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
        (GeoReplacedEntityRenderer<T, E, R> renderer)
        implements GeoRenderEvent.ReplacedEntity.CompileRenderLayers<T, E, R>, RecordEvent {
    public static final EventBus<CompileReplacedEntityRenderLayersEvent> BUS = EventBus.create(CompileReplacedEntityRenderLayersEvent.class);

    @Override
    public GeoReplacedEntityRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }
}
