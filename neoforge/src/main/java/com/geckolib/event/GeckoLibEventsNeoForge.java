package com.geckolib.event;

import com.geckolib.renderer.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.GeoItem;
import com.geckolib.event.armor.CompileArmorRenderLayersEvent;
import com.geckolib.event.armor.CompileArmorRenderStateEvent;
import com.geckolib.event.armor.GeoArmorPreRenderEvent;
import com.geckolib.event.block.CompileBlockRenderLayersEvent;
import com.geckolib.event.block.CompileBlockRenderStateEvent;
import com.geckolib.event.block.GeoBlockPreRenderEvent;
import com.geckolib.event.entity.CompileEntityRenderLayersEvent;
import com.geckolib.event.entity.CompileEntityRenderStateEvent;
import com.geckolib.event.entity.GeoEntityPreRenderEvent;
import com.geckolib.event.item.CompileItemRenderLayersEvent;
import com.geckolib.event.item.CompileItemRenderStateEvent;
import com.geckolib.event.item.GeoItemPreRenderEvent;
import com.geckolib.event.object.CompileObjectRenderLayersEvent;
import com.geckolib.event.object.CompileObjectRenderStateEvent;
import com.geckolib.event.object.GeoObjectPreRenderEvent;
import com.geckolib.event.replacedentity.CompileReplacedEntityRenderLayersEvent;
import com.geckolib.event.replacedentity.CompileReplacedEntityRenderStateEvent;
import com.geckolib.event.replacedentity.GeoReplacedEntityPreRenderEvent;
import com.geckolib.renderer.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.service.GeckoLibEvents;

/**
 * NeoForge service implementation for GeckoLib's various events
 */
public class GeckoLibEventsNeoForge implements GeckoLibEvents {
    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderLayers} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState>
    void fireCompileBlockRenderLayers(GeoBlockRenderer<T, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileBlockRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderState} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState>
    void fireCompileBlockRenderState(GeoBlockRenderer<T, R> renderer, R renderState, T animatable) {
        NeoForge.EVENT_BUS.post(new CompileBlockRenderStateEvent<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState>
    boolean fireBlockPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoBlockPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.CompileRenderLayers} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    void fireCompileArmorRenderLayers(GeoArmorRenderer<T, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileArmorRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends Item & GeoItem, O extends GeoArmorRenderer.RenderData, R extends HumanoidRenderState & GeoRenderState>
    void fireCompileArmorRenderState(GeoArmorRenderer<T, R> renderer, R renderState, T animatable, O renderData) {
        NeoForge.EVENT_BUS.post(new CompileArmorRenderStateEvent<>(renderer, renderState, animatable, renderData));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    boolean fireArmorPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoArmorPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderLayers} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireCompileEntityRenderLayers(GeoEntityRenderer<T, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileEntityRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderState} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireCompileEntityRenderState(GeoEntityRenderer<T, R> renderer, R renderState, T animatable) {
        NeoForge.EVENT_BUS.post(new CompileEntityRenderStateEvent<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    boolean fireEntityPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoEntityPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderLayers} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireCompileReplacedEntityRenderLayers(GeoReplacedEntityRenderer<T, E, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileReplacedEntityRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderState} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireCompileReplacedEntityRenderState(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, T animatable, E entity) {
        NeoForge.EVENT_BUS.post(new CompileReplacedEntityRenderStateEvent<>(renderer, renderState, animatable, entity));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    boolean fireReplacedEntityPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoReplacedEntityPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderLayers} event
     */
    @Override
    public <T extends Item & GeoAnimatable>
    void fireCompileItemRenderLayers(GeoItemRenderer<T> renderer) {
        NeoForge.EVENT_BUS.post(new CompileItemRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderState} event
     */
    @Override
    public <T extends Item & GeoAnimatable, O extends GeoItemRenderer.RenderData, R extends GeoRenderState>
    void fireCompileItemRenderState(GeoItemRenderer<T> renderer, R renderState, T animatable, O renderData) {
        NeoForge.EVENT_BUS.post(new CompileItemRenderStateEvent<>(renderer, renderState, animatable, renderData));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends Item & GeoAnimatable>
    boolean fireItemPreRender(RenderPassInfo<GeoRenderState> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoItemPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderLayers} event
     */
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState>
    void fireCompileObjectRenderLayers(GeoObjectRenderer<T, E, R> renderer) {
        NeoForge.EVENT_BUS.post(new CompileObjectRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderState} event
     */
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState>
    void fireCompileObjectRenderState(GeoObjectRenderer<T, E, R> renderer, R renderState, T animatable, @Nullable E relatedObject) {
        NeoForge.EVENT_BUS.post(new CompileObjectRenderStateEvent<>(renderer, renderState, animatable, relatedObject));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState>
    boolean fireObjectPreRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return !NeoForge.EVENT_BUS.post(new GeoObjectPreRenderEvent<>(renderPassInfo, renderTasks)).isCanceled();
    }
}
