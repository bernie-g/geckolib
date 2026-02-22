package com.geckolib.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibClientServices;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.GeoItem;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.geckolib.renderer.layer.GeoRenderLayersContainer;
import com.geckolib.util.ClientUtil;

import java.util.List;
import java.util.function.Function;

/// Base [GeoRenderer] class for rendering [Items][Item] specifically
///
/// All items added to be rendered by GeckoLib should use an instance of this class.
///
/// @param <T> Item animatable class type
public class GeoItemRenderer<T extends Item & GeoAnimatable> implements GeoRenderer<T, GeoItemRenderer.RenderData, GeoRenderState> {
	protected final GeoRenderLayersContainer<T, RenderData, GeoRenderState> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;
	protected boolean useEntityGuiLighting = false;

    /// Creates a new defaulted renderer instance, using the item's registered id as the file name for its assets
    public <I extends T> GeoItemRenderer(I item) {
        this(new DefaultedItemGeoModel<>(BuiltInRegistries.ITEM.getKey(item)));
    }

	public GeoItemRenderer(GeoModel<T> model) {
		this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(),
				model);
	}

	public GeoItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, GeoModel<T> model) {
		this.model = model;
	}

	/// Mark this renderer so that it uses an alternate lighting scheme when rendering the item in GUI
	///
	/// This can help with improperly lit 3d models
	public GeoItemRenderer<T> useAlternateGuiLighting() {
		this.useEntityGuiLighting = true;

		return this;
	}

    /// Set the current lighting normals for the current render pass
    ///
    /// Only used for [ItemDisplayContext#GUI] rendering
    public void setupLightingForGuiRender() {
		if (this.useEntityGuiLighting) {
			Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
		}
		else {
			Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		}
	}

	/// Data container for additional render context information for creating the RenderState for this renderer
	///
	/// @param itemStack The ItemStack about to be rendered
	/// @param vanillaRenderState The vanilla render state for the item stack. Not usually used for dynamic rendering
	/// @param renderPerspective The [ItemDisplayContext] that the item is being rendered in
	/// @param level The [ClientLevel] that the item is being rendered in, if applicable. A world being present doesn't necessarily mean the item physically is in the world itself
	/// @param itemOwner The associated entity, if applicable.
	public record RenderData(ItemStack itemStack, ItemStackRenderState vanillaRenderState, ItemDisplayContext renderPerspective, @Nullable ClientLevel level, @Nullable ItemOwner itemOwner) {}

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
    /// Gets the model instance for this renderer
    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

    /// Returns the list of registered [GeoRenderLayers][GeoRenderLayer] for this renderer
    @Override
    public List<GeoRenderLayer<T, RenderData, GeoRenderState>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    /// Adds a [GeoRenderLayer] to this renderer, to be called after the main model is rendered each frame
    @SuppressWarnings("UnusedReturnValue")
    public GeoItemRenderer<T> withRenderLayer(Function<? super GeoItemRenderer<T>, GeoRenderLayer<T, RenderData, GeoRenderState>> renderLayer) {
        return withRenderLayer(renderLayer.apply(this));
    }

    /// Adds a [GeoRenderLayer] to this renderer, to be called after the main model is rendered each frame
    public GeoItemRenderer<T> withRenderLayer(GeoRenderLayer<T, RenderData, GeoRenderState> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

    /// Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
    public GeoItemRenderer<T> withScale(float scale) {
        return withScale(scale, scale);
    }

    /// Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
    public GeoItemRenderer<T> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /// Gets the id that represents the current animatable's instance for animation purposes.
    ///
    /// You generally shouldn't need to override this
    ///
    /// @param animatable The Animatable instance being renderer
    /// @param renderData The associated render data for the animatable
    @ApiStatus.Internal
    @Override
    public long getInstanceId(T animatable, @SuppressWarnings("NullableProblems") RenderData renderData) {
        return GeoItem.getId(renderData.itemStack);
    }

    /// Internal method for capturing the common RenderState data for all animatable objects
    @ApiStatus.Internal
    @Override
    public void captureDefaultRenderState(T animatable, @SuppressWarnings("NullableProblems") RenderData renderData, GeoRenderState renderState, float partialTick) {
        GeoRenderer.super.captureDefaultRenderState(animatable, renderData, renderState, partialTick);

        ItemStack stack = renderData.itemStack;

        renderState.addGeckolibData(DataTickets.ITEM, animatable);
        renderState.addGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE, renderData.renderPerspective);
        renderState.addGeckolibData(DataTickets.IS_ENCHANTED, stack.isEnchanted());
        renderState.addGeckolibData(DataTickets.IS_STACKABLE, stack.isStackable());
        renderState.addGeckolibData(DataTickets.MAX_USE_DURATION, ClientUtil.getClientPlayer() != null ? stack.getUseDuration(ClientUtil.getClientPlayer()) : 72000);
        renderState.addGeckolibData(DataTickets.MAX_DURABILITY, stack.getMaxDamage());
        renderState.addGeckolibData(DataTickets.REMAINING_DURABILITY, stack.isDamageableItem() ? stack.getMaxDamage() - stack.getDamageValue() : 1);
    }

    /// Called at the start of the render compilation pass. PoseState manipulations have not yet taken place and typically should not be made here.
    ///
    /// Manipulation of the model's bones is not permitted here
    ///
    /// Use this method to handle any preparation or pre-work required for the render submission.
    ///
    /// @see #scaleModelForRender
    /// @see #adjustRenderPose
    /// @see #adjustModelBonesForRender
    @SuppressWarnings({"DataFlowIssue"})
    @Override
    public void preRenderPass(RenderPassInfo<GeoRenderState> renderPassInfo, SubmitNodeCollector renderTasks) {
        final GeoRenderState renderState = renderPassInfo.renderState();

        renderState.getGeckolibData(DataTickets.ANIMATABLE_MANAGER).setAnimatableData(DataTickets.ITEM_RENDER_PERSPECTIVE, renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE));
    }

    /// Scales the [PoseStack] in preparation for rendering the model, excluding when re-rendering the model as part of a [GeoRenderLayer] or external render call
    ///
    /// Override and call `super` with modified scale values as needed to further modify the scale of the model
    @Override
    public void scaleModelForRender(RenderPassInfo<GeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        GeoRenderer.super.scaleModelForRender(renderPassInfo, this.scaleWidth * widthScale, this.scaleHeight * heightScale);
    }

    /// Transform the [PoseStack] in preparation for rendering the model.
    ///
    /// This is called after [#scaleModelForRender], and so any transformations here will be scaled appropriately.
    /// If you need to do pre-scale translations, use [#preRenderPass]
    ///
    /// PoseStack translations made here are kept until the end of the render process
    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> renderPassInfo) {
        renderPassInfo.poseStack().translate(0.5f, 0.51f, 0.5f);
    }

    /// The entry render point for this renderer
    ///
    /// @param renderState The GeoRenderState for this render pass. This must be already compiled
    /// @param poseStack The PoseStack to render under
    /// @param renderTasks The render task collector for the render pass
    /// @param outlineColor The rendering outline color this render pass should apply (as if glowing)
    public void submit(GeoRenderState renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, int outlineColor) {
        renderState.addGeckolibData(DataTickets.GLOW_COLOUR, outlineColor);
        performRenderPass(renderState, poseStack, renderTasks, Minecraft.getInstance().gameRenderer.getLevelRenderState().cameraRenderState, null);
    }

    /// Called to create the [GeoRenderState] for this render pass
    @Override
    public GeoRenderState createRenderState(T animatable, @SuppressWarnings("NullableProblems") RenderData relatedObject) {
        return new GeoRenderState.Impl();
    }

    /// Create and fire the relevant `CompileLayers` event hook for this renderer
    @Override
    public void fireCompileRenderLayersEvent() {
        GeckoLibClientServices.EVENTS.fireCompileItemRenderLayers(this);
    }

    /// Create and fire the relevant `CompileRenderState` event hook for this renderer
    @Override
    public void fireCompileRenderStateEvent(T animatable, @SuppressWarnings("NullableProblems") RenderData renderData, GeoRenderState renderState, float partialTick) {
        GeckoLibClientServices.EVENTS.fireCompileItemRenderState(this, renderState, animatable, renderData);
    }

    /// Create and fire the relevant `Pre-Render` event hook for this renderer
    ///
    /// @return Whether the renderer should proceed based on the cancellation state of the event
    @Override
    public boolean firePreRenderEvent(RenderPassInfo<GeoRenderState> renderPassInfo, SubmitNodeCollector renderTasks) {
        return GeckoLibClientServices.EVENTS.fireItemPreRender(renderPassInfo, renderTasks);
    }
    //</editor-fold>
}
