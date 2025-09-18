package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/**
 * Base {@link GeoRenderer} class for rendering {@link Item Items} specifically
 * <p>
 * All items added to be rendered by GeckoLib should use an instance of this class.
 */
public class GeoItemRenderer<T extends Item & GeoAnimatable> extends BlockEntityWithoutLevelRenderer implements GeoRenderer<T> {
	protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected ItemStack currentItemStack;
	protected ItemDisplayContext renderPerspective;
	protected T animatable;
	protected float scaleWidth = 1;
	protected float scaleHeight = 1;
	protected boolean useEntityGuiLighting = false;

	protected Matrix4f itemRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

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
		super(dispatcher, modelSet);

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
	 * Gets the {@link GeoAnimatable} instance currently being rendered
	 */
	@Override
	public T getAnimatable() {
		return this.animatable;
	}

	/**
	 * Returns the current ItemStack being rendered
	 */
	public ItemStack getCurrentItemStack() {
		return this.currentItemStack;
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
	 * Gets the id that represents the current animatable's instance for animation purposes
	 * <p>
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	@Override
	public long getInstanceId(T animatable) {
		return GeoItem.getId(this.currentItemStack);
	}

	/**
	 * Shadowing override of {@link EntityRenderer#getTextureLocation}
	 * <p>
	 * This redirects the call to {@link GeoRenderer#getTextureLocation}
	 */
	@Override
	public ResourceLocation getTextureLocation(T animatable) {
		return GeoRenderer.super.getTextureLocation(animatable);
	}

	/**
	 * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
	 */
	@Override
	public List<GeoRenderLayer<T>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoItemRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
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
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());

		scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);

		if (!isReRender)
			poseStack.translate(0.5f, 0.51f, 0.5f);
	}

	@Override
	@ApiStatus.Internal
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		this.animatable = (T)stack.getItem();
		this.currentItemStack = stack;
		this.renderPerspective = transformType;
		float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);

		if (transformType == ItemDisplayContext.GUI) {
			renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay, partialTick);
		}
		else {
			RenderType renderType = getRenderType(this.animatable, getTextureLocation(this.animatable), bufferSource, partialTick);
			VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, false, this.currentItemStack != null && this.currentItemStack.hasFoil());

			defaultRender(poseStack, this.animatable, bufferSource, renderType, buffer,
					0, partialTick, packedLight);
		}

		this.animatable = null;
	}

	/**
	 * Wrapper method to handle rendering the item in a GUI context (defined by {@link ItemDisplayContext#GUI} normally)
	 * <p>
	 * Just includes some additional required transformations and settings
	 */
	protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack,
							   MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick) {
		setupLightingForGuiRender();

		MultiBufferSource.BufferSource defaultBufferSource = bufferSource instanceof MultiBufferSource.BufferSource bufferSource2 ? bufferSource2 : Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
		RenderType renderType = getRenderType(this.animatable, getTextureLocation(this.animatable), defaultBufferSource, partialTick);
		VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, this.currentItemStack != null && this.currentItemStack.hasFoil());

		poseStack.pushPose();
		defaultRender(poseStack, this.animatable, defaultBufferSource, renderType, buffer, 0, partialTick, packedLight);
		defaultBufferSource.endBatch();
		RenderSystem.enableDepthTest();
		Lighting.setupFor3DItems();
		poseStack.popPose();
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType,
							   MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
							   int packedLight, int packedOverlay, int colour) {

		if (!isReRender) {
			AnimationState<T> animationState = new AnimationState<>(animatable, 0, 0, partialTick, false);
			long instanceId = getInstanceId(animatable);
			GeoModel<T> currentModel = getGeoModel();

			animationState.setData(DataTickets.TICK, animatable.getTick(this.currentItemStack));
			animationState.setData(DataTickets.ITEM_RENDER_PERSPECTIVE, this.renderPerspective);
			animationState.setData(DataTickets.ITEMSTACK, this.currentItemStack);
			animatable.getAnimatableInstanceCache().getManagerForId(instanceId).setData(DataTickets.ITEM_RENDER_PERSPECTIVE, this.renderPerspective);
			currentModel.addAdditionalStateData(animatable, instanceId, animationState::setData);
			currentModel.handleAnimations(animatable, instanceId, animationState, partialTick);
		}

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (buffer != null)
			GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
					packedLight, packedOverlay, colour);
	}

	/**
	 * Called after all render operations are completed and the render pass is considered functionally complete.
	 * <p>
	 * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
	 */
	@Override
	public void doPostRenderCleanup() {
		this.animatable = null;
		this.currentItemStack = null;
		this.renderPerspective = null;
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
								  int packedOverlay, int colour) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.itemRenderTranslations));
		}

		GeoRenderer.super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay,
				colour);
	}

	/**
	 * Set the current lighting normals for the current render pass
	 * <p>
	 * Only used for {@link ItemDisplayContext#GUI} rendering
	 */
	public void setupLightingForGuiRender() {
		if (this.useEntityGuiLighting) {
			Lighting.setupForEntityInInventory();
		}
		else {
			Lighting.setupForFlatItems();
		}
	}

	/**
	 * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this GeoRenderer
	 * <p>
	 * This should only be called immediately prior to rendering
	 *
	 * @see AnimatableTexture#setAndUpdate
	 */
	@Override
	public void updateAnimatedTextureFrame(T animatable) {
		AnimatableTexture.setAndUpdate(getTextureLocation(animatable));
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileItemRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		return GeckoLibServices.Client.EVENTS.fireItemPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		GeckoLibServices.Client.EVENTS.fireItemPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
	}
}
