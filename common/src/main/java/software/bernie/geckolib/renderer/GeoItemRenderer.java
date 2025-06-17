package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/**
 * Base {@link GeoRenderer} class for rendering {@link Item Items} specifically
 * <p>
 * All items added to be rendered by GeckoLib should use an instance of this class.
 */
public class GeoItemRenderer<T extends Item & GeoAnimatable> implements GeoRenderer<T, ItemStack, GeoRenderState> {
	protected final GeoRenderLayersContainer<T, ItemStack, GeoRenderState> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;
	protected boolean useEntityGuiLighting = false;

	protected Matrix4f itemRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

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
	public List<GeoRenderLayer<T, ItemStack, GeoRenderState>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoItemRenderer<T> addRenderLayer(GeoRenderLayer<T, ItemStack, GeoRenderState> renderLayer) {
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
	 * @param itemStack The ItemStack about to be rendered
	 */
	@ApiStatus.Internal
	@Override
	public long getInstanceId(T animatable, ItemStack itemStack) {
		return GeoItem.getId(itemStack);
	}

	/**
	 * Internal method for capturing the common RenderState data for all animatable objects
	 */
	@ApiStatus.Internal
	@Override
	public GeoRenderState captureDefaultRenderState(T animatable, ItemStack itemStack, GeoRenderState renderState, float partialTick) {
		long instanceId = getInstanceId(animatable, itemStack);

		renderState.addGeckolibData(DataTickets.ITEM, animatable);
		renderState.addGeckolibData(DataTickets.TICK, animatable.getTick(animatable));
		renderState.addGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, instanceId);
		renderState.addGeckolibData(DataTickets.ANIMATABLE_MANAGER, animatable.getAnimatableInstanceCache().getManagerForId(instanceId));
		renderState.addGeckolibData(DataTickets.PARTIAL_TICK, partialTick);
		renderState.addGeckolibData(DataTickets.RENDER_COLOR, getRenderColor(animatable, itemStack, partialTick));
		renderState.addGeckolibData(DataTickets.IS_MOVING, false);
		renderState.addGeckolibData(DataTickets.BONE_RESET_TIME, animatable.getBoneResetTime());
		renderState.addGeckolibData(DataTickets.ANIMATABLE_CLASS, animatable.getClass());
		renderState.addGeckolibData(DataTickets.IS_ENCHANTED, itemStack.isEnchanted());
		renderState.addGeckolibData(DataTickets.IS_STACKABLE, itemStack.isStackable());
		renderState.addGeckolibData(DataTickets.MAX_USE_DURATION, itemStack.getUseDuration(ClientUtil.getClientPlayer()));
		renderState.addGeckolibData(DataTickets.MAX_DURABILITY, itemStack.getMaxDamage());
		renderState.addGeckolibData(DataTickets.REMAINING_DURABILITY, itemStack.isDamageableItem() ? itemStack.getMaxDamage() - itemStack.getDamageValue() : 1);
		renderState.addGeckolibData(DataTickets.PER_BONE_TASKS, new Reference2ObjectOpenHashMap<>(0));

		return renderState;
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if (!isReRender)
			this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());
	}

	/**
	 * Transform the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
	 */
	@Override
	public void adjustPositionForRender(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, boolean isReRender) {
		if (!isReRender)
			poseStack.translate(0.5f, 0.51f, 0.5f);
	}

	/**
	 * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
	 * <p>
	 * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
	 */
	@Override
	public void scaleModelForRender(GeoRenderState renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, boolean isReRender) {
		GeoRenderer.super.scaleModelForRender(renderState, widthScale * this.scaleWidth, heightScale * this.scaleHeight, poseStack, model, isReRender);
	}

	public void render(GeoRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource) {
		if (renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE) == ItemDisplayContext.GUI) {
			renderInGui(renderState, poseStack, bufferSource);
		}
		else {
			RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));
			VertexConsumer buffer = renderType == null ? null : ItemRenderer.getFoilBuffer(bufferSource, renderType, false, renderState.getGeckolibData(DataTickets.HAS_GLINT));

			defaultRender(renderState, poseStack, bufferSource, renderType, buffer);
		}
	}

	/**
	 * Wrapper method to handle rendering the item in a GUI context (defined by {@link ItemDisplayContext#GUI} normally)
	 * <p>
	 * Just includes some additional required transformations and settings
	 */
	protected void renderInGui(GeoRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource) {
		setupLightingForGuiRender();

		MultiBufferSource.BufferSource defaultBufferSource = bufferSource instanceof MultiBufferSource.BufferSource bufferSource2 ? bufferSource2 : Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
		RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));
		VertexConsumer buffer = ItemRenderer.getFoilBuffer(bufferSource, renderType, true, renderState.getGeckolibData(DataTickets.HAS_GLINT));

		poseStack.pushPose();
		defaultRender(renderState, poseStack, defaultBufferSource, renderType, buffer);
		defaultBufferSource.endBatch();
		Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		poseStack.popPose();
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType,
							   MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if (!isReRender) {
			((T)renderState.getGeckolibData(DataTickets.ITEM)).getAnimatableInstanceCache().getManagerForId(renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID)).setAnimatableData(DataTickets.ITEM_RENDER_PERSPECTIVE, renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE));
			getGeoModel().handleAnimations(createAnimationState(renderState));
		}

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (buffer != null)
			GeoRenderer.super.actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Called after all render operations are completed and the render pass is considered functionally complete.
	 * <p>
	 * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
	 */
	@Override
	public void doPostRenderCleanup() {
		this.itemRenderTranslations = null;
		this.modelRenderTranslations = null;
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(GeoRenderState renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.itemRenderTranslations));
		}

		GeoRenderer.super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
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
	public void fireCompileRenderStateEvent(T animatable, ItemStack itemStack, GeoRenderState renderState) {
		GeckoLibServices.Client.EVENTS.fireCompileItemRenderState(this, renderState, animatable, itemStack);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
		return GeckoLibServices.Client.EVENTS.fireItemPreRender(this, renderState, poseStack, model, bufferSource);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
		GeckoLibServices.Client.EVENTS.fireItemPostRender(this, renderState, poseStack, model, bufferSource);
	}
}
