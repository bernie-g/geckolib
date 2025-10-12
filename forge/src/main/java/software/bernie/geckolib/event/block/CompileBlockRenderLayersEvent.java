package software.bernie.geckolib.event.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
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
public record CompileBlockRenderLayersEvent<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState>
        (GeoBlockRenderer<T, R> renderer)
        implements GeoRenderEvent.Block.CompileRenderLayers<T, R>, RecordEvent {
    public static final EventBus<CompileBlockRenderLayersEvent> BUS = EventBus.create(CompileBlockRenderLayersEvent.class);

    @Override
    public GeoBlockRenderer<T, R> getRenderer() {
        return this.renderer;
    }
}
