package com.geckolib.event.armor;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import com.geckolib.animatable.GeoItem;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;

/**
 * One-time event for a {@link GeoBlockRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public record CompileArmorRenderLayersEvent<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
        (GeoArmorRenderer<T, R> renderer)
        implements GeoRenderEvent.Armor.CompileRenderLayers<T, R>, RecordEvent {
    public static final EventBus<CompileArmorRenderLayersEvent> BUS = EventBus.create(CompileArmorRenderLayersEvent.class);

    @Override
    public GeoArmorRenderer<T, R> getRenderer() {
        return this.renderer;
    }
}
