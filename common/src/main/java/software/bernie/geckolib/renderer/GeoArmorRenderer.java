package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.object.Color;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/**
 * Base {@link GeoRenderer} for rendering in-world armor specifically
 * <p>
 * All custom armor added to be rendered in-world by GeckoLib should use an instance of this class
 *
 * @see GeoItem
 */
public class GeoArmorRenderer<T extends Item & GeoItem> extends HumanoidModel implements GeoRenderer<T> {
	protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected T animatable;
	protected HumanoidModel<?> baseModel;
	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f entityRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

	protected BakedGeoModel lastModel = null;
	protected GeoBone head = null;
	protected GeoBone body = null;
	protected GeoBone rightArm = null;
	protected GeoBone leftArm = null;
	protected GeoBone rightLeg = null;
	protected GeoBone leftLeg = null;
	protected GeoBone rightBoot = null;
	protected GeoBone leftBoot = null;

	protected Entity currentEntity = null;
	protected ItemStack currentStack = null;
	protected EquipmentSlot currentSlot = null;
	protected MultiBufferSource bufferSource = null;
	protected float partialTick;
	protected float limbSwing;
	protected float limbSwingAmount;
	protected float netHeadYaw;
	protected float headPitch;

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
	 * Gets the {@link GeoItem} instance currently being rendered
	 */
	public T getAnimatable() {
		return this.animatable;
	}

	/**
	 * Returns the entity currently being rendered with armour equipped
	 */
	public Entity getCurrentEntity() {
		return this.currentEntity;
	}

	/**
	 * Returns the ItemStack pertaining to the current piece of armor being rendered
	 */
	public ItemStack getCurrentStack() {
		return this.currentStack;
	}

	/**
	 * Returns the equipped slot of the armor piece being rendered
	 */
	public EquipmentSlot getCurrentSlot() {
		return this.currentSlot;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes
	 * <p>
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	@Override
	public long getInstanceId(T animatable) {
		return GeoItem.getId(this.currentStack) + this.currentEntity.getId();
	}

	/**
	 * Gets the {@link RenderType} to render the given animatable with
	 * <p>
	 * Uses the {@link RenderType#armorCutoutNoCull} {@code RenderType} by default
	 * <p>
	 * Override this to change the way a model will render (such as translucent models, etc)
	 */
	@Override
	public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
		return RenderType.armorCutoutNoCull(texture);
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
	public GeoArmorRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
		this.renderLayers.addLayer(renderLayer);

		return this;
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoArmorRenderer<T> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoArmorRenderer<T> withScale(float scaleWidth, float scaleHeight) {
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;

		return this;
	}

	/**
	 * Returns the 'head' GeoBone from this model
	 * <p>
	 * Override if your geo model has different bone names for these bones
	 *
	 * @return The bone for the head model piece, or null if not using it
	 */
	@Nullable
	public GeoBone getHeadBone(GeoModel<T> model) {
		return model.getBone("armorHead").orElse(null);
	}

	/**
	 * Returns the 'body' GeoBone from this model
	 * <p>
	 * Override if your geo model has different bone names for these bones
	 *
	 * @return The bone for the body model piece, or null if not using it
	 */
	@Nullable
	public GeoBone getBodyBone(GeoModel<T> model) {
		return model.getBone("armorBody").orElse(null);
	}

	/**
	 * Returns the 'right arm' GeoBone from this model
	 * <p>
	 * Override if your geo model has different bone names for these bones
	 *
	 * @return The bone for the right arm model piece, or null if not using it
	 */
	@Nullable
	public GeoBone getRightArmBone(GeoModel<T> model) {
		return model.getBone("armorRightArm").orElse(null);
	}

	/**
	 * Returns the 'left arm' GeoBone from this model
	 * <p>
	 * Override if your geo model has different bone names for these bones
	 *
	 * @return The bone for the left arm model piece, or null if not using it
	 */
	@Nullable
	public GeoBone getLeftArmBone(GeoModel<T> model) {
		return model.getBone("armorLeftArm").orElse(null);
	}

	/**
	 * Returns the 'right leg' GeoBone from this model
	 * <p>
	 * Override if your geo model has different bone names for these bones
	 *
	 * @return The bone for the right leg model piece, or null if not using it
	 */
	@Nullable
	public GeoBone getRightLegBone(GeoModel<T> model) {
		return model.getBone("armorRightLeg").orElse(null);
	}

	/**
	 * Returns the 'left leg' GeoBone from this model
	 * <p>
	 * Override if your geo model has different bone names for these bones
	 *
	 * @return The bone for the left leg model piece, or null if not using it
	 */
	@Nullable
	public GeoBone getLeftLegBone(GeoModel<T> model) {
		return model.getBone("armorLeftLeg").orElse(null);
	}

	/**
	 * Returns the 'right boot' GeoBone from this model
	 * <p>
	 * Override if your geo model has different bone names for these bones
	 *
	 * @return The bone for the right boot model piece, or null if not using it
	 */
	@Nullable
	public GeoBone getRightBootBone(GeoModel<T> model) {
		return model.getBone("armorRightBoot").orElse(null);
	}

	/**
	 * Returns the 'left boot' GeoBone from this model
	 * <p>
	 * Override if your geo model has different bone names for these bones
	 *
	 * @return The bone for the left boot model piece, or null if not using it
	 */
	@Nullable
	public GeoBone getLeftBootBone(GeoModel<T> model) {
		return model.getBone("armorLeftBoot").orElse(null);
	}

	/**
	 * Gets a tint-applying color to render the given animatable with
	 * <p>
	 * Returns {@link Color#WHITE} by default
	 */
	@Override
	public Color getRenderColor(T animatable, float partialTick, int packedLight) {
		return this.currentStack.is(ItemTags.DYEABLE) ? Color.ofOpaque(DyedItemColor.getOrDefault(this.currentStack, -6265536)) : Color.WHITE;
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
						  @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
						  int packedOverlay, int renderColor) {
		this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

		applyBaseModel(this.baseModel);
		grabRelevantBones(model);
		applyBaseTransformations(this.baseModel);
		scaleModelForBaby(poseStack, animatable, partialTick, isReRender);
		scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);

		if (!(this.currentEntity instanceof GeoAnimatable)) {
			applyBoneVisibilityFromBaseModel(this.baseModel);
		}
	}

	@Override
	@ApiStatus.Internal
	public void renderToBuffer(PoseStack poseStack, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
		Minecraft mc = Minecraft.getInstance();
		MultiBufferSource bufferSource =  mc.levelRenderer.renderBuffers.bufferSource();

		if (mc.levelRenderer.shouldShowEntityOutlines() && mc.shouldEntityAppearGlowing(this.currentEntity))
			bufferSource =  mc.levelRenderer.renderBuffers.outlineBufferSource();

		float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		RenderType renderType = getRenderType(this.animatable, getTextureLocation(this.animatable), bufferSource, partialTick);
		buffer = renderType == null ? null : ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, this.currentStack.hasFoil());

		defaultRender(poseStack, this.animatable, bufferSource, null, buffer, partialTick, packedLight);

		this.animatable = null;
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType,
							   MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
							   int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();
		poseStack.translate(0, 24 / 16f, 0);
		poseStack.scale(-1, -1, 1);

		if (!isReRender) {
			long instanceId = getInstanceId(animatable);

			getGeoModel().handleAnimations(animatable, instanceId, createAnimationState(animatable, instanceId, 0, 0, partialTick, false), partialTick);
		}

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (buffer != null)
			GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
					packedLight, packedOverlay, renderColor);

		poseStack.popPose();
	}

	/**
	 * Construct the {@link AnimationState} for the given render pass, ready to pass onto the {@link GeoModel} for handling.
	 * <p>
	 * Override this method to add additional {@link software.bernie.geckolib.constant.DataTickets data} to the AnimationState as needed
	 */
	@Override
	public AnimationState<T> createAnimationState(T animatable, long instanceId, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving) {
		AnimationState<T> animationState = GeoRenderer.super.createAnimationState(animatable, instanceId, limbSwing, limbSwingAmount, partialTick, isMoving);

		animationState.setData(DataTickets.TICK, animatable.getTick(this.currentEntity));
		animationState.setData(DataTickets.ITEMSTACK, this.currentStack);
		animationState.setData(DataTickets.ENTITY, this.currentEntity);
		animationState.setData(DataTickets.EQUIPMENT_SLOT, this.currentSlot);

		return animationState;
	}

	/**
	 * Called after all render operations are completed and the render pass is considered functionally complete.
	 * <p>
	 * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
	 */
	@Override
	public void doPostRenderCleanup() {
		this.baseModel = null;
		this.currentEntity = null;
		this.currentStack = null;
		this.animatable = null;
		this.currentSlot = null;
		this.bufferSource = null;
		this.partialTick = 0;
		this.limbSwing = 0;
		this.limbSwingAmount = 0;
		this.netHeadYaw = 0;
		this.headPitch = 0;
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
								  int packedOverlay, int renderColor) {
		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations));
		}

		GeoRenderer.super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Gets and caches the relevant armor model bones for this baked model if it hasn't been done already
	 */
	protected void grabRelevantBones(BakedGeoModel bakedModel) {
		if (this.lastModel == bakedModel)
			return;

		GeoModel<T> model = getGeoModel();
		this.lastModel = bakedModel;
		this.head = getHeadBone(model);
		this.body = getBodyBone(model);
		this.rightArm = getRightArmBone(model);
		this.leftArm = getLeftArmBone(model);
		this.rightLeg = getRightLegBone(model);
		this.leftLeg = getLeftLegBone(model);
		this.rightBoot = getRightBootBone(model);
		this.leftBoot = getLeftBootBone(model);
	}

	/**
	 * Prepare the renderer for the current render pass
	 * <p>
	 * Must be called prior to render as the default HumanoidModel doesn't give render context
	 *
	 * @param entity The entity being rendered with the armor on
	 * @param stack The ItemStack being rendered
	 * @param slot The slot being rendered
	 * @param baseModel The default (vanilla) model that would have been rendered if this model hadn't replaced it
	 * @param bufferSource The buffer supplier for the current render context
	 * @param partialTick The fraction of a tick passed since the last game tick
	 * @param netHeadYaw The entity's Y rotation, discounting any head rotation
	 * @param headPitch The entity's X rotation
	 */
	public void prepForRender(Entity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> baseModel, MultiBufferSource bufferSource,
							  float partialTick, float netHeadYaw, float headPitch) {
		this.baseModel = baseModel;
		this.currentEntity = entity;
		this.currentStack = stack;
		this.animatable = (T)stack.getItem();
		this.currentSlot = slot;
		this.bufferSource = bufferSource;
		this.partialTick = partialTick;
		this.netHeadYaw = netHeadYaw;
		this.headPitch = headPitch;
	}

	/**
	 * Applies settings and transformations pre-render based on the default model
	 */
	protected void applyBaseModel(HumanoidModel<?> baseModel) {
		//this.young = baseModel.young;
		//this.crouching = baseModel.crouching;
		//this.riding = baseModel.riding;
		//this.rightArmPose = baseModel.rightArmPose;
		//this.leftArmPose = baseModel.leftArmPose;
	}

	/**
	 * Resets the bone visibility for the model based on the currently rendering slot,
	 * and then sets bones relevant to the current slot as visible for rendering
	 * <p>
	 * This is only called by default for non-geo entities (I.E. players or vanilla mobs)
	 */
	protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
		setAllVisible(false);

		switch (currentSlot) {
			case HEAD -> setBoneVisible(this.head, true);
			case CHEST -> {
				setBoneVisible(this.body, true);
				setBoneVisible(this.rightArm, true);
				setBoneVisible(this.leftArm, true);
			}
			case LEGS -> {
				setBoneVisible(this.rightLeg, true);
				setBoneVisible(this.leftLeg, true);
			}
			case FEET -> {
				setBoneVisible(this.rightBoot, true);
				setBoneVisible(this.leftBoot, true);
			}
			default -> {}
		}
	}
	
	protected void applyBoneVisibilityFromBaseModel(HumanoidModel<?> model) {
		setBoneVisible(this.head, model.head.visible);
		setBoneVisible(this.body, model.body.visible);
		setBoneVisible(this.rightArm, model.rightArm.visible);
		setBoneVisible(this.leftArm, model.leftArm.visible);
		setBoneVisible(this.rightLeg, model.rightLeg.visible);
		setBoneVisible(this.leftLeg, model.leftLeg.visible);
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
			bone = this.head;
		}
		else if (currentPart == model.body) {
			bone = this.body;
		}
		else if (currentPart == model.leftArm) {
			bone = this.leftArm;
		}
		else if (currentPart == model.rightArm) {
			bone = this.rightArm;
		}
		else if (currentPart == model.leftLeg) {
			bone = currentSlot == EquipmentSlot.FEET ? this.leftBoot : this.leftLeg;
		}
		else if (currentPart == model.rightLeg) {
			bone = currentSlot == EquipmentSlot.FEET ? this.rightBoot : this.rightLeg;
		}

		if (bone != null)
			bone.setHidden(false);
	}

	/**
	 * Transform the currently rendering {@link GeoModel} to match the positions and rotations of the base model
	 */
	protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
		if (this.head != null) {
			ModelPart headPart = baseModel.head;

			RenderUtil.matchModelPartRot(headPart, this.head);
			this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
		}

		if (this.body != null) {
			ModelPart bodyPart = baseModel.body;

			RenderUtil.matchModelPartRot(bodyPart, this.body);
			this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
		}

		if (this.rightArm != null) {
			ModelPart rightArmPart = baseModel.rightArm;

			RenderUtil.matchModelPartRot(rightArmPart, this.rightArm);
			this.rightArm.updatePosition(rightArmPart.x + 5, 2 - rightArmPart.y, rightArmPart.z);
		}

		if (this.leftArm != null) {
			ModelPart leftArmPart = baseModel.leftArm;

			RenderUtil.matchModelPartRot(leftArmPart, this.leftArm);
			this.leftArm.updatePosition(leftArmPart.x - 5f, 2f - leftArmPart.y, leftArmPart.z);
		}

		if (this.rightLeg != null) {
			ModelPart rightLegPart = baseModel.rightLeg;

			RenderUtil.matchModelPartRot(rightLegPart, this.rightLeg);
			this.rightLeg.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);

			if (this.rightBoot != null) {
				RenderUtil.matchModelPartRot(rightLegPart, this.rightBoot);
				this.rightBoot.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);
			}
		}

		if (this.leftLeg != null) {
			ModelPart leftLegPart = baseModel.leftLeg;

			RenderUtil.matchModelPartRot(leftLegPart, this.leftLeg);
			this.leftLeg.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);

			if (this.leftBoot != null) {
				RenderUtil.matchModelPartRot(leftLegPart, this.leftBoot);
				this.leftBoot.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);
			}
		}
	}

	@Override
	public void setAllVisible(boolean pVisible) {
		super.setAllVisible(pVisible);

		setBoneVisible(this.head, pVisible);
		setBoneVisible(this.body, pVisible);
		setBoneVisible(this.rightArm, pVisible);
		setBoneVisible(this.leftArm, pVisible);
		setBoneVisible(this.rightLeg, pVisible);
		setBoneVisible(this.leftLeg, pVisible);
		setBoneVisible(this.rightBoot, pVisible);
		setBoneVisible(this.leftBoot, pVisible);
	}

	/**
	 * Apply custom scaling to account for AgeableListModel baby models
	 */
	public void scaleModelForBaby(PoseStack poseStack, T animatable, float partialTick, boolean isReRender) {
		/*if (!this.young || isReRender)
			return;

		if (this.currentSlot == EquipmentSlot.HEAD) {
			if (this.baseModel.scaleHead) {
				float headScale = 1.5f / this.baseModel.babyHeadScale;

				poseStack.scale(headScale, headScale, headScale);
			}

			poseStack.translate(0, this.baseModel.babyYHeadOffset / 16f, this.baseModel.babyZHeadOffset / 16f);
		}
		else {
			float bodyScale = 1 / this.baseModel.babyBodyScale;

			poseStack.scale(bodyScale, bodyScale, bodyScale);
			poseStack.translate(0, this.baseModel.bodyYOffset / 16f, 0);
		}*/
	}

	/**
	 * Sets a bone as visible or hidden, with nullability
	 */
	protected void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
		if (bone == null)
			return;

		bone.setHidden(!visible);
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileArmorRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		return GeckoLibServices.Client.EVENTS.fireArmorPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		GeckoLibServices.Client.EVENTS.fireArmorPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
	}
}