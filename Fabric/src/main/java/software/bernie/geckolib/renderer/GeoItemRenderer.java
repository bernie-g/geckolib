package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.List;

/**
 * Base {@link GeoRenderer} class for rendering {@link Item Items} specifically.<br>
 * All items added to be rendered by GeckoLib should use an instance of this class.
 */
public class GeoItemRenderer<T extends Item & GeoAnimatable> extends BlockEntityWithoutLevelRenderer implements GeoRenderer<T> {
	protected final List<GeoRenderLayer<T>> renderLayers = new ObjectArrayList<>();
	protected final GeoModel<T> model;

	protected ItemStack currentItemStack;
	protected ItemDisplayContext renderPerspective;
	protected T animatable;
	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f itemRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

	public GeoItemRenderer(GeoModel<T> model) {
		this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(),
				model);
	}

	public GeoItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, GeoModel<T> model) {
		super(dispatcher, modelSet);

		this.model = model;
		
		fireCompileRenderLayersEvent();
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
	 * Gets the id that represents the current animatable's instance for animation purposes.
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	@Override
	public long getInstanceId(T animatable) {
		return GeoItem.getId(this.currentItemStack);
	}

	/**
	 * Shadowing override of {@link EntityRenderer#getTextureLocation}.<br>
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
		return this.renderLayers;
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoItemRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
		this.renderLayers.add(renderLayer);

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
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory
	 * work such as scaling and translating.<br>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
						  float alpha) {
		this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (this.scaleWidth != 1 && this.scaleHeight != 1)
			poseStack.scale(this.scaleWidth, this.scaleHeight, this.scaleWidth);

		poseStack.translate(0.5f, 0.51f, 0.5f);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		this.animatable = (T)stack.getItem();
		this.currentItemStack = stack;
		this.renderPerspective = transformType;

		if (transformType == ItemDisplayContext.GUI) {
			renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay);
		}
		else {
			RenderType renderType = getRenderType(this.animatable, getTextureLocation(this.animatable), bufferSource, Minecraft.getInstance().getFrameTime());
			VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, false, this.currentItemStack != null && this.currentItemStack.hasFoil());

			defaultRender(poseStack, this.animatable, bufferSource, renderType, buffer,
					0, Minecraft.getInstance().getFrameTime(), packedLight);
		}
	}

	/**
	 * Wrapper method to handle rendering the item in a GUI context
	 * (defined by {@link ItemDisplayContext#GUI} normally).<br>
	 * Just includes some additional required transformations and settings.
	 */
	protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack,
							   MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		MultiBufferSource.BufferSource defaultBufferSource = bufferSource instanceof MultiBufferSource.BufferSource bufferSource2 ?
				bufferSource2 : Minecraft.getInstance().renderBuffers().bufferSource();
		RenderType renderType = getRenderType(this.animatable, getTextureLocation(this.animatable), defaultBufferSource, Minecraft.getInstance().getFrameTime());
		VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, this.currentItemStack != null && this.currentItemStack.hasFoil());

		poseStack.pushPose();
		Lighting.setupForFlatItems();
		defaultRender(poseStack, this.animatable, defaultBufferSource, renderType, buffer,
				0, Minecraft.getInstance().getFrameTime(), packedLight);
		defaultBufferSource.endBatch();
		RenderSystem.enableDepthTest();
		Lighting.setupFor3DItems();
		poseStack.popPose();
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
							   MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
							   int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();

		if (!isReRender) {
			AnimationState<T> animationState = new AnimationState<>(animatable, 0, 0, partialTick, false);
			long instanceId = getInstanceId(animatable);

			animationState.setData(DataTickets.TICK, animatable.getTick(this.currentItemStack));
			animationState.setData(DataTickets.ITEM_RENDER_PERSPECTIVE, this.renderPerspective);
			animationState.setData(DataTickets.ITEMSTACK, this.currentItemStack);
			this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);
			this.model.handleAnimations(animatable, instanceId, animationState);
		}

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
				packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
								  int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

			bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.itemRenderTranslations));
		}

		GeoRenderer.super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeoRenderEvent.Item.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Item.CompileRenderLayers(this));
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer.<br>
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		return GeoRenderEvent.Item.Pre.EVENT.invoker().handle(new GeoRenderEvent.Item.Pre(this, poseStack, model, bufferSource, partialTick, packedLight));
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		GeoRenderEvent.Item.Post.EVENT.invoker().handle(new GeoRenderEvent.Item.Post(this, poseStack, model, bufferSource, partialTick, packedLight));
	}
}
