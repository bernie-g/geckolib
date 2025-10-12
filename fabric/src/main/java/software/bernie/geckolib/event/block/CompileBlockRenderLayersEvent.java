package software.bernie.geckolib.event.block;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * One-time event for a {@link GeoArmorRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see Armor.CompileRenderLayers
 */
public class CompileBlockRenderLayersEvent<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> implements GeoRenderEvent.Block.CompileRenderLayers<T, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoBlockRenderer<T, R> renderer;

    public CompileBlockRenderLayersEvent(GeoBlockRenderer<T, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoBlockRenderer<T, R> getRenderer() {
        return this.renderer;
    }

    /**
     * Event listener interface for the {@link CompileBlockRenderLayersEvent} GeoRenderEvent
     */
    @FunctionalInterface
    public interface Listener<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> {
        void handle(CompileBlockRenderLayersEvent<T, R> event);
    }
}
