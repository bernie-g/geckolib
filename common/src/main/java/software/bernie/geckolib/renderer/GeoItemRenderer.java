package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.function.Function;

/**
 * Base {@link GeoRenderer} class for rendering {@link Item Items} specifically
 * <p>
 * All items added to be rendered by GeckoLib should use an instance of this class.
 */
public class GeoItemRenderer<T extends Item & GeoAnimatable> implements GeoRenderer<T, GeoItemRenderer.RenderData, GeoRenderState> {
	protected final GeoRenderLayersContainer<T, RenderData, GeoRenderState> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;
	protected boolean useEntityGuiLighting = false;

    /**
     * Creates a new defaulted renderer instance, using the item's registered id as the file name for its assets
     */
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

	/**
	 * Gets the model instance for this renderer
	 */
	@Override
	public GeoModel<T> getGeoModel() {
		return this.model;
	}

	/**
	 * Mark this renderer so that it uses an alternate lighting scheme when rendering the item in GUI
	 * <p>
	 * This can help with improperly lit 3d models
	 */
	public GeoItemRenderer<T> useAlternateGuiLighting() {
		this.useEntityGuiLighting = true;

		return this;
	}

	/**
	 * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
	 */
	@Override
	public List<GeoRenderLayer<T, RenderData, GeoRenderState>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoItemRenderer<T> withRenderLayer(Function<? super GeoItemRenderer<T>, GeoRenderLayer<T, RenderData, GeoRenderState>> renderLayer) {
        return withRenderLayer(renderLayer.apply(this));
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoItemRenderer<T> withRenderLayer(GeoRenderLayer<T, RenderData, GeoRenderState> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoItemRenderer<T> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoItemRenderer<T> withScale(float scaleWidth, float scaleHeight) {
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;

		return this;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes.
	 * <p>
	 * You generally shouldn't need to override this
	 *
	 * @param animatable The Animatable instance being renderer
	 * @param renderData The associated render data for the animatable
	 */
	@ApiStatus.Internal
	@Override
	public long getInstanceId(T animatable, RenderData renderData) {
		return GeoItem.getId(renderData.itemStack);
	}

	/**
	 * Internal method for capturing the common RenderState data for all animatable objects
	 */
	@ApiStatus.Internal
	@Override
	public GeoRenderState captureDefaultRenderState(T animatable, RenderData renderData, GeoRenderState renderState, float partialTick) {
		long instanceId = getInstanceId(animatable, renderData);
		ItemStack stack = renderData.itemStack;

		renderState.addGeckolibData(DataTickets.ITEM, animatable);
		renderState.addGeckolibData(DataTickets.TICK, animatable.getTick(animatable));
		renderState.addGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, instanceId);
		renderState.addGeckolibData(DataTickets.ANIMATABLE_MANAGER, animatable.getAnimatableInstanceCache().getManagerForId(instanceId));
		renderState.addGeckolibData(DataTickets.PARTIAL_TICK, partialTick);
		renderState.addGeckolibData(DataTickets.RENDER_COLOR, getRenderColor(animatable, renderData, partialTick));
		renderState.addGeckolibData(DataTickets.IS_MOVING, false);
		renderState.addGeckolibData(DataTickets.BONE_RESET_TIME, animatable.getBoneResetTime());
		renderState.addGeckolibData(DataTickets.ANIMATABLE_CLASS, animatable.getClass());
		renderState.addGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE, renderData.renderPerspective);
		renderState.addGeckolibData(DataTickets.IS_ENCHANTED, stack.isEnchanted());
		renderState.addGeckolibData(DataTickets.IS_STACKABLE, stack.isStackable());
		renderState.addGeckolibData(DataTickets.MAX_USE_DURATION, stack.getUseDuration(ClientUtil.getClientPlayer()));
		renderState.addGeckolibData(DataTickets.MAX_DURABILITY, stack.getMaxDamage());
		renderState.addGeckolibData(DataTickets.REMAINING_DURABILITY, stack.isDamageableItem() ? stack.getMaxDamage() - stack.getDamageValue() : 1);
		renderState.addGeckolibData(DataTickets.PER_BONE_TASKS, new Reference2ObjectOpenHashMap<>(0));

		return renderState;
	}

    /**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */

	@Override
    public void preRender(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor) {
        renderState.addGeckolibData(DataTickets.OBJECT_RENDER_POSE, new Matrix4f(poseStack.last().pose()));

        ((T)renderState.getGeckolibData(DataTickets.ITEM)).getAnimatableInstanceCache().getManagerForId(renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID))
                .setAnimatableData(DataTickets.ITEM_RENDER_PERSPECTIVE, renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE));
	}

	/**
	 * Transform the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
	 */
	@Override
    public void adjustRenderPose(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
        poseStack.translate(0.5f, 0.51f, 0.5f);
	}

	/**
	 * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
	 * <p>
	 * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
	 */
	@Override
    public void scaleModelForRender(GeoRenderState renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
		GeoRenderer.super.scaleModelForRender(renderState, widthScale * this.scaleWidth, heightScale * this.scaleHeight, poseStack, model, cameraState);
	}

    /**
     * The entry render point for this renderer
     *
     * @param renderState The GeoRenderState for this render pass. This must be already compiled
     * @param poseStack The PoseStack to render under
     * @param renderTasks The render task collector for the render pass
     * @param outlineColor The rendering outline colour this render should apply (as if glowing)
     */
    public void submit(GeoRenderState renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, int outlineColor) {
        renderState.addGeckolibData(DataTickets.GLOW_COLOUR, outlineColor);
        submitRenderTasks(renderState, poseStack, renderTasks, Minecraft.getInstance().gameRenderer.getLevelRenderState().cameraRenderState);



        /*if (renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE) == ItemDisplayContext.GUI) {
            renderInGui(renderState, poseStack, bufferSource);
        }
        else {
            RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));
            VertexConsumer buffer = renderType == null ? null : ItemRenderer.getFoilBuffer(bufferSource, renderType, false, renderState.getGeckolibData(DataTickets.HAS_GLINT));

            submitRenderTasks(renderState, poseStack, bufferSource, renderType, buffer);
        }*/
    }

/*	*//**
	 * Wrapper method to handle rendering the item in a GUI context (defined by {@link ItemDisplayContext#GUI} normally)
	 * <p>
	 * Just includes some additional required transformations and settings
	 *//*
	protected void renderInGui(GeoRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource) {
		setupLightingForGuiRender();

		MultiBufferSource.BufferSource defaultBufferSource = bufferSource instanceof MultiBufferSource.BufferSource bufferSource2 ? bufferSource2 : Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
		RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));
		VertexConsumer buffer = ItemRenderer.getFoilBuffer(bufferSource, renderType, true, renderState.getGeckolibData(DataTickets.HAS_GLINT));

		poseStack.pushPose();
		submitRenderTasks(renderState, poseStack, defaultBufferSource, renderType, buffer);
		defaultBufferSource.endBatch();
		Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		poseStack.popPose();
	}*/

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
    public void buildRenderTask(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, OrderedSubmitNodeCollector renderTasks, CameraRenderState cameraState,
                                @Nullable RenderType renderType, int packedLight, int packedOverlay, int renderColor) {
        renderState.addGeckolibData(DataTickets.MODEL_RENDER_POSE, new Matrix4f(poseStack.last().pose()));

        if (renderType == null)
            return;

        GeoRenderer.super.buildRenderTask(renderState, poseStack, model, renderTasks, cameraState, renderType, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
    public void renderBone(GeoRenderState renderState, PoseStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState, boolean skipBoneTasks,
                           int packedLight, int packedOverlay, int renderColor) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, renderState.getGeckolibData(DataTickets.OBJECT_RENDER_POSE)));
            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, renderState.getGeckolibData(DataTickets.MODEL_RENDER_POSE)));
		}

		GeoRenderer.super.renderBone(renderState, poseStack, bone, buffer, cameraState, skipBoneTasks, packedLight, packedOverlay, renderColor);
	}

    /**
     * Called to create the {@link GeoRenderState} for this render pass
     */
    @Override
    public GeoRenderState createRenderState(T animatable, RenderData relatedObject) {
        return new GeoRenderState.Impl();
    }

    /**
	 * Set the current lighting normals for the current render pass
	 * <p>
	 * Only used for {@link ItemDisplayContext#GUI} rendering
	 */
	public void setupLightingForGuiRender() {
		if (this.useEntityGuiLighting) {
			Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
		}
		else {
			Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		}
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileItemRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderStateEvent(T animatable, RenderData renderData, GeoRenderState renderState, float partialTick) {
		GeckoLibServices.Client.EVENTS.fireCompileItemRenderState(this, renderState, animatable, renderData);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
		return GeckoLibServices.Client.EVENTS.fireItemPreRender(this, renderState, poseStack, model, renderTasks, cameraState);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
    public void firePostRenderEvent(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
		GeckoLibServices.Client.EVENTS.fireItemPostRender(this, renderState, poseStack, model, renderTasks, cameraState);
	}

	/**
	 * Data container for additional render context information for creating the RenderState for this renderer
	 *
	 * @param itemStack The ItemStack about to be rendered
	 * @param vanillaRenderState The vanilla render state for the item stack. Not usually used for dynamic rendering
	 * @param renderPerspective The {@link ItemDisplayContext} that the item is being rendered in
	 * @param level The {@link ClientLevel} that the item is being rendered in, if applicable. A world being present doesn't necessarily mean the item physically is in the world itself
	 * @param itemOwner The associated entity, if applicable.
	 */
	public record RenderData(ItemStack itemStack, ItemStackRenderState vanillaRenderState, ItemDisplayContext renderPerspective, @Nullable ClientLevel level, @Nullable ItemOwner itemOwner) {}
}
