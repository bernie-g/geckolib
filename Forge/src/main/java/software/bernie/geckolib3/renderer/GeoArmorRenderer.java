package software.bernie.geckolib3.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.animatable.GeoArmor;
import software.bernie.geckolib3.animatable.GeoItem;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.cache.object.GeoBone;
import software.bernie.geckolib3.constant.DataTickets;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.model.GeoModel;
import software.bernie.geckolib3.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base {@link GeoRenderer} for rendering in-world {@link GeoArmor Armor} specifically.<br>
 * All custom armor added to be rendered in-world by GeckoLib should use an instance of this class.
 * @see software.bernie.geckolib3.item.GeoArmorItem
 * @param <T>
 */
public class GeoArmorRenderer<T extends Item & GeoArmor> extends HumanoidModel implements GeoRenderer<T> {
	protected final List<GeoRenderLayer<T>> renderLayers = new ObjectArrayList<>();
	protected final GeoModel<T> model;

	protected T animatable;

	protected Matrix4f renderStartPose = new Matrix4f();
	protected Matrix4f preRenderPose = new Matrix4f();

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
	 * Gets the {@link GeoArmor} instance currently being rendered
	 */
	public T getAnimatable() {
		return this.animatable;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes.
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	@Override
	public long getInstanceId(T animatable) {
		return GeoItem.getId(this.currentStack);
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
	public GeoArmorRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
		this.renderLayers.add(renderLayer);

		return this;
	}

	/**
	 * Returns the 'head' GeoBone from this model.<br>
	 * Override if your geo model has different bone names for these bones
	 * @return The bone for the head model piece, or null if not using it
	 */
	@Nullable
	protected GeoBone getHeadBone() {
		return this.model.getBone("armorHead").orElse(null);
	}

	/**
	 * Returns the 'body' GeoBone from this model.<br>
	 * Override if your geo model has different bone names for these bones
	 * @return The bone for the body model piece, or null if not using it
	 */
	@Nullable
	protected GeoBone getBodyBone() {
		return this.model.getBone("armorBody").orElse(null);
	}

	/**
	 * Returns the 'right arm' GeoBone from this model.<br>
	 * Override if your geo model has different bone names for these bones
	 * @return The bone for the right arm model piece, or null if not using it
	 */
	@Nullable
	protected GeoBone getRightArmBone() {
		return this.model.getBone("armorRightArm").orElse(null);
	}

	/**
	 * Returns the 'left arm' GeoBone from this model.<br>
	 * Override if your geo model has different bone names for these bones
	 * @return The bone for the left arm model piece, or null if not using it
	 */
	@Nullable
	protected GeoBone getLeftArmBone() {
		return this.model.getBone("armorLeftArm").orElse(null);
	}

	/**
	 * Returns the 'right leg' GeoBone from this model.<br>
	 * Override if your geo model has different bone names for these bones
	 * @return The bone for the right leg model piece, or null if not using it
	 */
	@Nullable
	protected GeoBone getRightLegBone() {
		return this.model.getBone("armorRightLeg").orElse(null);
	}

	/**
	 * Returns the 'left leg' GeoBone from this model.<br>
	 * Override if your geo model has different bone names for these bones
	 * @return The bone for the left leg model piece, or null if not using it
	 */
	@Nullable
	protected GeoBone getLeftLegBone() {
		return this.model.getBone("armorLeftLeg").orElse(null);
	}

	/**
	 * Returns the 'right boot' GeoBone from this model.<br>
	 * Override if your geo model has different bone names for these bones
	 * @return The bone for the right boot model piece, or null if not using it
	 */
	@Nullable
	protected GeoBone getRightBootBone() {
		return this.model.getBone("armorRightBoot").orElse(null);
	}

	/**
	 * Returns the 'left boot' GeoBone from this model.<br>
	 * Override if your geo model has different bone names for these bones
	 * @return The bone for the left boot model piece, or null if not using it
	 */
	@Nullable
	protected GeoBone getLeftBootBone() {
		return this.model.getBone("armorLeftBoot").orElse(null);
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory
	 * work such as scaling and translating.<br>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
						  @Nullable VertexConsumer buffer, float partialTick, int packedLight,
						  int packedOverlay, float red, float green, float blue, float alpha) {
		this.preRenderPose = poseStack.last().pose().copy();

		applyBoneVisibility(this.currentSlot);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
							   int packedOverlay, float red, float green, float blue, float alpha) {
		Minecraft mc = Minecraft.getInstance();
		MultiBufferSource bufferSource = mc.renderBuffers().bufferSource();

		if (mc.levelRenderer.shouldShowEntityOutlines() && mc.shouldEntityAppearGlowing(this.currentEntity))
			bufferSource = mc.renderBuffers().outlineBufferSource();

		defaultRender(poseStack, this.animatable, bufferSource, null, buffer,
				0, Minecraft.getInstance().getFrameTime(), packedLight);
	}

	/**
	 * The actual render method that sub-type renderers should override to handle their specific rendering tasks.<br>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
							   MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
							   int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		poseStack.translate(0, 24 / 16f, 0);
		poseStack.scale(-1, -1, 1);

		this.renderStartPose = poseStack.last().pose().copy();
		AnimationEvent<T> animationEvent = new AnimationEvent<>(animatable, 0, 0, partialTick, false);
		long instanceId = getInstanceId(animatable);

		animationEvent.setData(DataTickets.ITEMSTACK, this.currentStack);
		animationEvent.setData(DataTickets.ENTITY, this.currentEntity);
		animationEvent.setData(DataTickets.EQUIPMENT_SLOT, this.currentSlot);
		this.model.addAdditionalEventData(animatable, instanceId, animationEvent::setData);
		this.model.handleAnimations(animatable, instanceId, animationEvent);
		GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight,
								  int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose();

			bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.preRenderPose));
			bone.setLocalSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderStartPose));
		}

		GeoRenderer.super.renderRecursively(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	/**
	 * Gets and caches the relevant armor model bones for this baked model if it hasn't been done already
	 */
	protected void grabRelevantBones(BakedGeoModel bakedModel) {
		if (this.lastModel == bakedModel)
			return;

		this.lastModel = bakedModel;
		this.head = getHeadBone();
		this.body = getBodyBone();
		this.rightArm = getRightArmBone();
		this.leftArm = getLeftArmBone();
		this.rightLeg = getRightLegBone();
		this.leftLeg = getLeftLegBone();
		this.rightBoot = getRightBootBone();
		this.leftBoot = getLeftBootBone();
	}

	/**
	 * Prepare the renderer for the current render cycle.<br>
	 * Must be called prior to render as the default HumanoidModel doesn't give render context.
	 * @param entity The entity being rendered with the armor on
	 * @param stack The ItemStack being rendered
	 * @param slot The slot being rendered
	 * @param baseModel The default (vanilla) model that would have been rendered if this model hadn't replaced it
	 */
	public void prepForRender(Entity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> baseModel) {
		this.currentEntity = entity;
		this.currentStack = stack;
		this.animatable = (T)stack.getItem();
		this.currentSlot = slot;

		applyBaseModel(baseModel);
		grabRelevantBones(getGeoModel().getBakedModel(getGeoModel().getModelResource(this.animatable)));
		applyBaseTransformations(baseModel);
	}

	/**
	 * Applies settings and transformations pre-render based on the default model
	 */
	protected void applyBaseModel(HumanoidModel<?> baseModel) {
		this.young = baseModel.young;
		this.crouching = baseModel.crouching;
		this.riding = baseModel.riding;
		this.rightArmPose = baseModel.rightArmPose;
		this.leftArmPose = baseModel.leftArmPose;
	}

	/**
	 * Resets the bone visibility for the model, and then sets the current slot as visible for rendering.
	 */
	protected void applyBoneVisibility(EquipmentSlot currentSlot) {
		setBoneVisible(this.head, false);
		setBoneVisible(this.body, false);
		setBoneVisible(this.rightArm, false);
		setBoneVisible(this.leftArm, false);
		setBoneVisible(this.rightLeg, false);
		setBoneVisible(this.leftLeg, false);
		setBoneVisible(this.rightBoot, false);
		setBoneVisible(this.leftBoot, false);

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

	/**
	 * Transform the currently rendering {@link GeoModel} to match the positions and rotations of the base model
	 */
	protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
		if (this.head != null) {
			ModelPart headPart = baseModel.head;

			RenderUtils.matchModelPartRot(headPart, this.head);
			this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
		}

		if (this.body != null) {
			ModelPart bodyPart = baseModel.body;

			RenderUtils.matchModelPartRot(bodyPart, this.body);
			this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
		}

		if (this.rightArm != null) {
			ModelPart rightArmPart = baseModel.rightArm;

			RenderUtils.matchModelPartRot(rightArmPart, this.rightArm);
			this.rightArm.updatePosition(rightArmPart.x + 5, 2 - rightArmPart.y, rightArmPart.z);
		}

		if (this.leftArm != null) {
			ModelPart leftArmPart = baseModel.leftArm;

			RenderUtils.matchModelPartRot(leftArmPart, this.leftArm);
			this.leftArm.updatePosition(leftArmPart.x - 5, 2 - leftArmPart.y, leftArmPart.z);
		}

		if (this.rightLeg != null) {
			ModelPart rightLegPart = baseModel.rightLeg;

			RenderUtils.matchModelPartRot(rightLegPart, this.rightLeg);
			this.rightLeg.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);

			if (this.rightBoot != null) {
				RenderUtils.matchModelPartRot(rightLegPart, this.rightBoot);
				this.rightBoot.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);
			}
		}

		if (this.leftLeg != null) {
			ModelPart leftLegPart = baseModel.leftLeg;

			RenderUtils.matchModelPartRot(leftLegPart, this.leftLeg);
			this.leftLeg.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);

			if (this.leftBoot != null) {
				RenderUtils.matchModelPartRot(leftLegPart, this.leftBoot);
				this.leftBoot.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);
			}
		}
	}

	/**
	 * Sets a bone as visible or hidden, with nullability
	 */
	protected void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
		if (bone == null)
			return;

		bone.setHidden(!visible);
	}
}