package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.processing.AnimationProcessor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Base {@link GeoRenderer} for rendering in-world armor specifically
 * <p>
 * All custom armor added to be rendered in-world by GeckoLib should use an instance of this class
 *
 * @see GeoItem
 */
public class GeoArmorRenderer<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends HumanoidModel implements GeoRenderer<T, GeoArmorRenderer.RenderData, R> {
	protected final GeoRenderLayersContainer<T, GeoArmorRenderer.RenderData, R> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f entityRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

	protected BakedGeoModel lastModel = null;
	protected GeoBone headBone = null;
	protected GeoBone bodyBone = null;
	protected GeoBone rightArmBone = null;
	protected GeoBone leftArmBone = null;
	protected GeoBone rightLegBone = null;
	protected GeoBone leftLegBone = null;
	protected GeoBone rightBootBone = null;
	protected GeoBone leftBootBone = null;

	public GeoArmorRenderer(GeoModel<T> model) {
		super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));

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
	 * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
	 */
	@Override
	public List<GeoRenderLayer<T, RenderData, R>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoArmorRenderer<T, R> addRenderLayer(GeoRenderLayer<T, RenderData, R> renderLayer) {
		this.renderLayers.addLayer(renderLayer);

		return this;
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoArmorRenderer<T, R> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoArmorRenderer<T, R> withScale(float scaleWidth, float scaleHeight) {
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
	 * @param stackAndSlot An object related to the render pass or null if not applicable.
	 *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 */
	@ApiStatus.Internal
	@Override
	public long getInstanceId(T animatable, RenderData stackAndSlot) {
		long stackId = GeoItem.getId(stackAndSlot.itemStack());

		if (stackId == Long.MAX_VALUE) {
			int id = stackAndSlot.entity().getId() * 13;

			return (long)id * id * id * -(stackAndSlot.slot().ordinal() + 1);
		}

		return -stackId;
	}

	/**
	 * Gets a tint-applying color to render the given animatable with
	 * <p>
	 * Returns white (no tint) by default
	 */
	@Override
	public int getRenderColor(T animatable, RenderData stackAndSlot, float partialTick) {
		return GeckoLibServices.Client.ITEM_RENDERING.getDyedItemColor(stackAndSlot.itemStack(), 0xFFFFFFFF);
	}

	/**
	 * Gets the {@link RenderType} to render the given animatable with
	 * <p>
	 * Uses the {@link RenderType#armorCutoutNoCull} {@code RenderType} by default
	 * <p>
	 * Override this to change the way a model will render (such as translucent models, etc)
	 */
	@Nullable
	@Override
	public RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.armorCutoutNoCull(texture);
	}

	/**
	 * Internal method for capturing the common RenderState data for all animatable objects
	 */
	@ApiStatus.Internal
	@Override
	public R captureDefaultRenderState(T animatable, RenderData renderData, R renderState, float partialTick) {
		GeoRenderer.super.captureDefaultRenderState(animatable, renderData, renderState, partialTick);

		renderState.addGeckolibData(DataTickets.IS_GECKOLIB_WEARER, renderData.entity() instanceof GeoAnimatable);
		renderState.addGeckolibData(DataTickets.EQUIPMENT_SLOT, renderData.slot());
		renderState.addGeckolibData(DataTickets.HAS_GLINT, renderData.itemStack().hasFoil());
		renderState.addGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, renderState.isInvisibleToPlayer);
		renderState.addGeckolibData(DataTickets.IS_GLOWING, renderState.appearsGlowing);

		return renderState;
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
						  boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if (!isReRender) {
			this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

			applyBaseTransformations(renderState.getGeckolibData(DataTickets.HUMANOID_MODEL));
			applyBaseModel(renderState.getGeckolibData(DataTickets.HUMANOID_MODEL));
			grabRelevantBones(model);

			if (!renderState.getGeckolibData(DataTickets.IS_GECKOLIB_WEARER))
				applyBoneVisibilityBySlot(renderState.getGeckolibData(DataTickets.EQUIPMENT_SLOT));
		}
	}

	/**
	 * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
	 * <p>
	 * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
	 */
	@Override
	public void scaleModelForRender(R renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, boolean isReRender) {
		GeoRenderer.super.scaleModelForRender(renderState, widthScale * this.scaleWidth, heightScale * this.scaleHeight, poseStack, model, isReRender);
	}

	/**
	 * Should never be used
	 */
	@Override
	@ApiStatus.Internal
	public final void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
		GeckoLibConstants.LOGGER.debug("Something is attempting to directly call HumanoidModel#renderToBuffer. This is not supported by GeoArmorRenderer.");
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();
		poseStack.translate(0, 24 / 16f, 0);
		poseStack.scale(-1, -1, 1);

		if (!isReRender)
			getGeoModel().handleAnimations(createAnimationState(renderState));

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (buffer != null)
			GeoRenderer.super.actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);

		poseStack.popPose();
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(R renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
								  int packedLight, int packedOverlay, int renderColor) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations));
		}

		GeoRenderer.super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Gets and caches the relevant armor model bones for this baked model if it hasn't been done already
	 */
	protected void grabRelevantBones(BakedGeoModel bakedModel) {
		if (this.lastModel == bakedModel)
			return;

		AnimationProcessor<T> animationProcessor = this.model.getAnimationProcessor();
		this.lastModel = bakedModel;
		this.headBone = animationProcessor.getBone("armorHead");
		this.bodyBone = animationProcessor.getBone("armorBody");
		this.rightArmBone = animationProcessor.getBone("armorRightArm");
		this.leftArmBone = animationProcessor.getBone("armorLeftArm");
		this.rightLegBone = animationProcessor.getBone("armorRightLeg");
		this.leftLegBone = animationProcessor.getBone("armorLeftLeg");
		this.rightBootBone = animationProcessor.getBone("armorRightBoot");
		this.leftBootBone = animationProcessor.getBone("armorLeftBoot");
	}

	/**
	 * Applies settings and transformations pre-render based on the default model
	 */
	protected void applyBaseModel(HumanoidModel<?> baseModel) {
		HumanoidModel<?> self = (HumanoidModel<?>)this;

		self.head.visible = baseModel.head.visible;
		self.hat.visible = baseModel.hat.visible;
		self.body.visible = baseModel.body.visible;
		self.rightArm.visible = baseModel.rightArm.visible;
		self.leftArm.visible = baseModel.leftArm.visible;
		self.rightLeg.visible = baseModel.rightLeg.visible;
		self.leftLeg.visible = baseModel.leftLeg.visible;
	}

	/**
	 * Resets the bone visibility for the model based on the currently rendering slot,
	 * and then sets bones relevant to the current slot as visible for rendering
	 * <p>
	 * This is only called by default for non-geo entities (I.E. players or vanilla humanoid mobs)
	 */
	protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
		setAllBonesVisible(false);

		switch (currentSlot) {
			case HEAD -> setBonesVisible(this.head.visible, this.headBone);
			case CHEST -> setBonesVisible(this.body.visible, this.bodyBone, this.rightArmBone, this.leftArmBone);
			case LEGS -> setBonesVisible(this.rightLeg.visible, this.rightLegBone, this.leftLegBone);
			case FEET -> setBonesVisible(this.rightLeg.visible, this.rightBootBone, this.leftBootBone);
			default -> {}
		}
	}

	/**
	 * Resets the bone visibility for the model based on the current {@link ModelPart} and {@link EquipmentSlot},
	 * and then sets the bones relevant to the current part as visible for rendering
	 * <p>
	 * If you are rendering a geo entity with armor, you should probably be calling this prior to rendering
	 */
	public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
		setAllVisible(false);

		currentPart.visible = true;
		GeoBone bone = null;

		if (currentPart == model.hat || currentPart == model.head) {
			bone = this.headBone;
		}
		else if (currentPart == model.body) {
			bone = this.bodyBone;
		}
		else if (currentPart == model.leftArm) {
			bone = this.leftArmBone;
		}
		else if (currentPart == model.rightArm) {
			bone = this.rightArmBone;
		}
		else if (currentPart == model.leftLeg) {
			bone = currentSlot == EquipmentSlot.FEET ? this.leftBootBone : this.leftLegBone;
		}
		else if (currentPart == model.rightLeg) {
			bone = currentSlot == EquipmentSlot.FEET ? this.rightBootBone : this.rightLegBone;
		}

		if (bone != null)
			bone.setHidden(false);
	}

	/**
	 * Transform the currently rendering {@link GeoModel} to match the positions and rotations of the base model
	 */
	protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
		if (this.headBone != null) {
			ModelPart headPart = baseModel.head;

			RenderUtil.matchModelPartRot(headPart, this.headBone);
			this.headBone.updatePosition(headPart.x, -headPart.y, headPart.z);
		}

		if (this.bodyBone != null) {
			ModelPart bodyPart = baseModel.body;

			RenderUtil.matchModelPartRot(bodyPart, this.bodyBone);
			this.bodyBone.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
		}

		if (this.rightArmBone != null) {
			ModelPart rightArmPart = baseModel.rightArm;

			RenderUtil.matchModelPartRot(rightArmPart, this.rightArmBone);
			this.rightArmBone.updatePosition(rightArmPart.x + 5, 2 - rightArmPart.y, rightArmPart.z);
		}

		if (this.leftArmBone != null) {
			ModelPart leftArmPart = baseModel.leftArm;

			RenderUtil.matchModelPartRot(leftArmPart, this.leftArmBone);
			this.leftArmBone.updatePosition(leftArmPart.x - 5f, 2f - leftArmPart.y, leftArmPart.z);
		}

		if (this.rightLegBone != null) {
			ModelPart rightLegPart = baseModel.rightLeg;

			RenderUtil.matchModelPartRot(rightLegPart, this.rightLegBone);
			this.rightLegBone.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);

			if (this.rightBootBone != null) {
				RenderUtil.matchModelPartRot(rightLegPart, this.rightBootBone);
				this.rightBootBone.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);
			}
		}

		if (this.leftLegBone != null) {
			ModelPart leftLegPart = baseModel.leftLeg;

			RenderUtil.matchModelPartRot(leftLegPart, this.leftLegBone);
			this.leftLegBone.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);

			if (this.leftBootBone != null) {
				RenderUtil.matchModelPartRot(leftLegPart, this.leftBootBone);
				this.leftBootBone.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);
			}
		}
	}

	@Override
	public void setAllVisible(boolean visible) {
		super.setAllVisible(visible);
		setAllBonesVisible(visible);
	}

	/**
	 * Equivalent to {@link HumanoidModel#setAllVisible(boolean)}, but explicitly only for the GeoBones.
	 * <p>
	 * This allows for resetting of model visibility whilst still allowing inheritance of visibility from the {@link HumanoidArmorLayer}
	 */
	protected void setAllBonesVisible(boolean visible) {
		setBonesVisible(visible, this.headBone, this.bodyBone, this.rightArmBone, this.leftArmBone,
						this.rightLegBone, this.leftLegBone, this.rightBootBone, this.leftBootBone);
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileArmorRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderStateEvent(T animatable, RenderData relatedObject, R renderState) {
		GeckoLibServices.Client.EVENTS.fireCompileArmorRenderState(this, renderState, animatable, relatedObject);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer.<br>
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
		return GeckoLibServices.Client.EVENTS.fireArmorPreRender(this, renderState, poseStack, model, bufferSource);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
		GeckoLibServices.Client.EVENTS.fireArmorPostRender(this, renderState, poseStack, model, bufferSource);
	}

	/**
	 * Data container for additional render context information for creating the RenderState for this renderer
	 *
	 * @param itemStack The ItemStack about to be rendered
	 * @param slot The EquipmentSlot the ItemStack is in
	 * @param entity The entity wearing the item
	 */
	public record RenderData(ItemStack itemStack, EquipmentSlot slot, LivingEntity entity) {}

	/**
	 * Attempt to render a GeckoLib {@link GeoArmorRenderer armor piece} for the given slot
	 * <p>
	 * This is typically only called by an internal mixin
	 *
	 * @return true if the armor piece was a GeckoLib armor piece and rendered
	 */
	@ApiStatus.Internal
	public static <S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> boolean tryRenderGeoArmorPiece(
			PoseStack poseStack, MultiBufferSource bufferSource, S humanoidRenderState, ItemStack stack, EquipmentSlot equipmentSlot,
			M parentModel, A baseModel, int packedLight, float netHeadYaw, float headPitch, BiConsumer<A, EquipmentSlot> partVisibilitySetter) {
		GeoRenderProvider renderProvider;

		if (!HumanoidArmorLayer.shouldRender(stack, equipmentSlot) || (renderProvider = GeoRenderProvider.of(stack)) == GeoRenderProvider.DEFAULT || !(humanoidRenderState instanceof GeoRenderState geoRenderState))
			return false;

		final GeoArmorRenderer armorRenderer = renderProvider.getGeoArmorRenderer(humanoidRenderState, stack, equipmentSlot,
																							   equipmentSlot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID, baseModel);
		if (armorRenderer == null)
			return false;

		Map<DataTicket<?>, Object> existingRenderData = null;

		EnumMap<EquipmentSlot, Map<DataTicket<?>, Object>> perSlotData = geoRenderState.hasGeckolibData(DataTickets.PER_SLOT_RENDER_DATA) ? geoRenderState.getGeckolibData(DataTickets.PER_SLOT_RENDER_DATA) : null;

		geoRenderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);

		if (perSlotData != null && perSlotData.containsKey(equipmentSlot)) {
			Map<DataTicket<?>, Object> renderData = geoRenderState.getDataMap();
			existingRenderData = new Reference2ObjectOpenHashMap<>(renderData);
			renderData.clear();
			renderData.putAll(perSlotData.get(equipmentSlot));
		}

		geoRenderState.addGeckolibData(DataTickets.HUMANOID_MODEL, baseModel);
		geoRenderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);

		parentModel.copyPropertiesTo(baseModel);
		partVisibilitySetter.accept(baseModel, equipmentSlot);
		baseModel.copyPropertiesTo(armorRenderer);

		RenderType renderType = armorRenderer.getRenderType(humanoidRenderState, armorRenderer.getTextureLocation(geoRenderState));
		VertexConsumer buffer = renderType == null ? null : ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, geoRenderState.getGeckolibData(DataTickets.HAS_GLINT));

		armorRenderer.defaultRender(geoRenderState, poseStack, bufferSource, renderType, buffer);

		if (existingRenderData != null) {
			geoRenderState.getDataMap().clear();
			geoRenderState.getDataMap().putAll(existingRenderData);
		}

		return true;
	}

	/**
	 * Internal method for capturing the cross-slot renderstate data for {@link GeoArmorRenderer}s
	 */
	@ApiStatus.Internal
	public static <R extends HumanoidRenderState & GeoRenderState> void captureRenderStates(R baseRenderState, LivingEntity entity, float partialTick) {
		EnumMap<EquipmentSlot, Pair<GeoArmorRenderer<?, ?>, ItemStack>> relevantSlots = getRelevantSlotsForRendering(entity);

		if (relevantSlots == null)
			return;

		EnumMap<EquipmentSlot, Map<DataTicket<?>, Object>> slotRenderData = new EnumMap<>(EquipmentSlot.class);
		Map<DataTicket<?>, Object> dataMap = baseRenderState.getDataMap();

		for (EnumMap.Entry<EquipmentSlot, Pair<GeoArmorRenderer<?, ?>, ItemStack>> entry : relevantSlots.entrySet()) {
			GeoArmorRenderer renderer = entry.getValue().left();
			ItemStack stack = entry.getValue().right();
			RenderData renderData = new RenderData(stack, entry.getKey(), entity);

			renderer.fillRenderState((GeoAnimatable)stack.getItem(), renderData, baseRenderState, partialTick);
			slotRenderData.put(entry.getKey(), new Reference2ObjectOpenHashMap<>(dataMap));
			dataMap.clear();
		}

		baseRenderState.addGeckolibData(DataTickets.PER_SLOT_RENDER_DATA, slotRenderData);
	}

	@Nullable
	@ApiStatus.Internal
	private static EnumMap<EquipmentSlot, Pair<GeoArmorRenderer<?, ?>, ItemStack>> getRelevantSlotsForRendering(LivingEntity entity) {
		EnumMap<EquipmentSlot, Pair<GeoArmorRenderer<?, ?>, ItemStack>> relevantSlots = null;

		for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
			GeoRenderProvider geoRenderProvider;
			ItemStack stack = HumanoidMobRenderer.getEquipmentIfRenderable(entity, slot);

			if (stack.isEmpty() || (geoRenderProvider = GeoRenderProvider.of(stack)) == GeoRenderProvider.DEFAULT)
				continue;

			GeoArmorRenderer<?, ?> renderer = geoRenderProvider
													  .getGeoArmorRenderer(null, stack, slot,
																		   slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS :
																		   EquipmentClientInfo.LayerType.HUMANOID, null) instanceof GeoArmorRenderer<?, ?> geoArmorRenderer ? geoArmorRenderer : null;

			if (renderer != null) {
				if (relevantSlots == null)
					relevantSlots = new EnumMap<>(EquipmentSlot.class);

				relevantSlots.put(slot, Pair.of(renderer, stack));
			}
		}

		return relevantSlots;
	}
}