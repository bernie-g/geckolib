package com.geckolib.event.armor;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
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
public class CompileArmorRenderLayersEvent<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Event implements GeoRenderEvent.Armor.CompileRenderLayers<T, R> {
    private final GeoArmorRenderer<T, R> renderer;

    public CompileArmorRenderLayersEvent(GeoArmorRenderer<T, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoArmorRenderer<T, R> getRenderer() {
        return this.renderer;
    }
}
