package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
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
 * Base {@link GeoRenderer} class for rendering {@link Entity Entities} specifically
 * <p>
 * All entities added to be rendered by GeckoLib should use an instance of this class
 * <p>
 * This also includes {@link net.minecraft.world.entity.projectile.Projectile Projectiles}
 */
public class GeoEntityRenderer<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends EntityRenderer<T, EntityRenderState> implements GeoRenderer<T, Void, R> {
	protected final GeoRenderLayersContainer<T, Void, R> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;
	protected final ItemModelResolver itemModelResolver;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f entityRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

	public GeoEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
		super(context);

		this.model = model;
		this.itemModelResolver = context.getItemModelResolver();
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
	public List<GeoRenderLayer<T, Void, R>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoEntityRenderer<T, R> addRenderLayer(GeoRenderLayer<T, Void, R> renderLayer) {
		this.renderLayers.addLayer(renderLayer);

		return this;
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoEntityRenderer<T, R> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoEntityRenderer<T, R> withScale(float scaleWidth, float scaleHeight) {
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
	 * @param relatedObject An object related to the render pass or null if not applicable.
	 *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 */
	@ApiStatus.Internal
	@Override
	public long getInstanceId(T animatable, Void relatedObject) {
		return animatable.getId();
	}

	/**
	 * Gets a tint-applying color to render the given animatable with
	 * <p>
	 * Returns opaque white by default, modified for invisibility in spectator
	 */
	@Override
	public int getRenderColor(T animatable, Void relatedObject, float partialTick) {
		int color = GeoRenderer.super.getRenderColor(animatable, relatedObject, partialTick);

		if (animatable.isInvisible() && !animatable.isInvisibleTo(ClientUtil.getClientPlayer()))
			color = ARGB.color(Mth.ceil(ARGB.alpha(color) * 38 / 255f), color);

		return color;
	}

	/**
	 * Gets a packed overlay coordinate pair for rendering
	 * <p>
	 * Mostly just used for the red tint when an entity is hurt,
	 * but can be used for other things like the {@link net.minecraft.world.entity.monster.Creeper}
	 * white tint when exploding.
	 */
	@Override
	public int getPackedOverlay(T animatable, Void relatedObject, float u, float partialTick) {
		if (!(animatable instanceof LivingEntity entity))
			return OverlayTexture.NO_OVERLAY;

		return OverlayTexture.pack(OverlayTexture.u(u),
								   OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
	}

	/**
	 * Get the maximum distance (in blocks) that an entity's nameplate should be visible when it is sneaking
	 * <p>
	 * This is only a short-circuit predicate, and other conditions after this check must be also passed in order for the name to render
	 * <p>
	 * This is hard-capped at a maximum of 256 blocks regardless of what this method returns
	 */
	public double getNameRenderCutoffDistance(T animatable) {
		return 32d;
	}

	/**
	 * Whether the entity's nametag should be rendered or not
	 * <p>
	 * Used to determine nametag attachment in {@link EntityRenderer#extractRenderState(Entity, EntityRenderState, float)}
	 */
	@Override
	public boolean shouldShowName(T animatable, double distToCameraSq) {
		if (!(animatable instanceof LivingEntity))
			return super.shouldShowName(animatable, distToCameraSq);

		if (animatable.isDiscrete()) {
			double nameRenderCutoff = getNameRenderCutoffDistance(animatable);

			if (distToCameraSq >= nameRenderCutoff * nameRenderCutoff)
				return false;
		}

		if (animatable instanceof Mob && (!animatable.shouldShowName() && (!animatable.hasCustomName() || animatable != this.entityRenderDispatcher.crosshairPickEntity)))
			return false;

		final Minecraft minecraft = Minecraft.getInstance();
		boolean visibleToClient = !animatable.isInvisibleTo(ClientUtil.getClientPlayer());
		Team entityTeam = animatable.getTeam();

		if (entityTeam == null)
			return Minecraft.renderNames() && animatable != minecraft.getCameraEntity() && visibleToClient && !animatable.isVehicle();

		Team playerTeam = ClientUtil.getClientPlayer().getTeam();

		return switch (entityTeam.getNameTagVisibility()) {
			case ALWAYS -> visibleToClient;
			case NEVER -> false;
			case HIDE_FOR_OTHER_TEAMS -> playerTeam == null ? visibleToClient : entityTeam.isAlliedTo(playerTeam) && (entityTeam.canSeeFriendlyInvisibles() || visibleToClient);
			case HIDE_FOR_OWN_TEAM -> playerTeam == null ? visibleToClient : !entityTeam.isAlliedTo(playerTeam) && visibleToClient;
		};
	}

	/**
	 * Calculate the yaw of the given animatable.
	 * <p>
	 * Normally only called for non-{@link LivingEntity LivingEntities}, and shouldn't be considered a safe place to modify rotation<br>
	 * Do that in {@link #addRenderData(GeoAnimatable, Object, GeoRenderState)} instead
	 */
	protected final float calculateYRot(T animatable, float yHeadRot, float partialTick) {
		if (!(animatable.getVehicle() instanceof LivingEntity vehicle))
			return animatable.getVisualRotationYInDegrees();

		float vehicleRotation = Mth.rotLerp(partialTick, vehicle.yBodyRotO, vehicle.yBodyRot);
		float clampedVehicleRotation = Mth.clamp(Mth.wrapDegrees(-vehicleRotation), -85, 85);
		vehicleRotation = yHeadRot - vehicleRotation;

		if (Math.abs(clampedVehicleRotation) > 50)
			vehicleRotation += clampedVehicleRotation * 0.2f;

		return vehicleRotation;
	}

	/**
	 * Gets the {@link RenderType} to render the given animatable with
	 * <p>
	 * Uses the {@link RenderType#entityCutoutNoCull} {@code RenderType} by default
	 * <p>
	 * Override this to change the way a model will render (such as translucent models, etc).
	 *
	 * @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions taking place
	 */
	@Nullable
	@Override
	public RenderType getRenderType(R renderState, ResourceLocation texture) {
		if (renderState.isInvisible && !renderState.getGeckolibData(DataTickets.INVISIBLE_TO_PLAYER))
			return RenderType.itemEntityTranslucentCull(texture);

		if (!renderState.isInvisible)
			return GeoRenderer.super.getRenderType(renderState, texture);

		return renderState.getGeckolibData(DataTickets.IS_GLOWING) ? RenderType.outline(texture) : null;
	}

	/**
	 * Internal method for capturing the common RenderState data for all animatable objects
	 */
	@ApiStatus.Internal
	@Override
	public R captureDefaultRenderState(T animatable, Void relatedObject, R renderState, float partialTick) {
		GeoRenderer.super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);

		LivingEntityRenderState livingRenderState = renderState instanceof LivingEntityRenderState state ? state : null;

		renderState.addGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, livingRenderState != null ? livingRenderState.isInvisibleToPlayer : animatable.isInvisible() && animatable.isInvisibleTo(ClientUtil.getClientPlayer()));
		renderState.addGeckolibData(DataTickets.IS_GLOWING, livingRenderState != null ? livingRenderState.appearsGlowing : Minecraft.getInstance().shouldEntityAppearGlowing(animatable));
		renderState.addGeckolibData(DataTickets.IS_SHAKING, livingRenderState != null ? livingRenderState.isFullyFrozen : animatable.isFullyFrozen());
		renderState.addGeckolibData(DataTickets.ENTITY_POSE, livingRenderState != null ? livingRenderState.pose : animatable.getPose());
		renderState.addGeckolibData(DataTickets.ENTITY_PITCH, livingRenderState != null ? livingRenderState.xRot : animatable.getXRot(partialTick));
		renderState.addGeckolibData(DataTickets.ENTITY_YAW, livingRenderState != null ? livingRenderState.yRot : calculateYRot(animatable, 0, partialTick));
		renderState.addGeckolibData(DataTickets.ENTITY_BODY_YAW, livingRenderState != null ? livingRenderState.bodyRot : renderState.getGeckolibData(DataTickets.ENTITY_YAW));
		renderState.addGeckolibData(DataTickets.VELOCITY, animatable.getDeltaMovement());
		renderState.addGeckolibData(DataTickets.BLOCKPOS, animatable.blockPosition());
		renderState.addGeckolibData(DataTickets.POSITION, livingRenderState != null ? new Vec3(livingRenderState.x, livingRenderState.y, livingRenderState.z) : animatable.getPosition(1));
		renderState.addGeckolibData(DataTickets.SPRINTING, animatable.isSprinting());
		renderState.addGeckolibData(DataTickets.IS_MOVING, (animatable instanceof LivingEntity livingEntity ? livingEntity.walkAnimation.speed() : animatable.getDeltaMovement().lengthSqr())  >= getMotionAnimThreshold(animatable));

		if (animatable instanceof LivingEntity livingEntity) {
			renderState.addGeckolibData(DataTickets.SWINGING_ARM, livingEntity.swinging);
			renderState.addGeckolibData(DataTickets.IS_DEAD_OR_DYING, livingEntity.isDeadOrDying());
		}

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
		this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

		scaleModelForRender(renderState, this.scaleWidth, this.scaleHeight, poseStack, model, isReRender);
	}

	@ApiStatus.Internal
	@Override
	public void render(EntityRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		defaultRender((R)renderState, poseStack, bufferSource, null, null);
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType,
							   MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if (!isReRender) {
			LivingEntityRenderState livingRenderState =  renderState instanceof LivingEntityRenderState state ? state : null;
			float nativeScale = livingRenderState != null ? livingRenderState.scale : 1;

			if (renderState.getGeckolibData(DataTickets.ENTITY_POSE) == Pose.SLEEPING && livingRenderState != null) {
				Direction bedDirection = livingRenderState.bedOrientation;

				if (bedDirection != null) {
					float eyePosOffset = livingRenderState.eyeHeight - 0.1F;

					poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
				}
			}

			poseStack.scale(nativeScale, nativeScale, nativeScale);
			applyRotations(renderState, poseStack, nativeScale);
			getGeoModel().handleAnimations(createAnimationState(renderState));
			poseStack.translate(0, 0.01f, 0);
		}

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (buffer != null)
			GeoRenderer.super.actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Call after all other rendering work has taken place, including reverting the {@link PoseStack}'s state
	 * <p>
	 * This method is <u>not</u> called in {@link GeoRenderer#reRender re-render}
	 */
	@Override
	public void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer) {
		super.render(renderState, poseStack, bufferSource, renderState.getGeckolibData(DataTickets.PACKED_LIGHT));
	}

	/**
	 * Called after all render operations are completed, and the render pass is considered functionally complete.
	 * <p>
	 * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
	 */
	@Override
	public void doPostRenderCleanup() {
		this.entityRenderTranslations = null;
		this.modelRenderTranslations = null;
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(R renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
								  boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();
		RenderUtil.translateMatrixToBone(poseStack, bone);
		RenderUtil.translateToPivotPoint(poseStack, bone);
		RenderUtil.rotateMatrixAroundBone(poseStack, bone);
		RenderUtil.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());
			Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(RenderUtil.translateMatrix(localMatrix, getRenderOffset(renderState).toVector3f()));
			bone.setWorldSpaceMatrix(RenderUtil.translateMatrix(new Matrix4f(localMatrix), new Vector3f((float)renderState.x, (float)renderState.y, (float)renderState.z)));
		}

		RenderUtil.translateAwayFromPivotPoint(poseStack, bone);

		renderCubesOfBone(renderState, bone, poseStack, buffer, packedLight, packedOverlay, renderColor);

		if (!isReRender)
			applyRenderLayersForBone(renderState, bone, poseStack, renderType, bufferSource);

		renderChildBones(renderState, bone, poseStack, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);

		poseStack.popPose();
	}

	/**
	 * Applies rotation transformations to the renderer prior to render time to account for various entity states
	 */
	protected void applyRotations(R renderState, PoseStack poseStack, float nativeScale) {
		float rotationYaw = renderState.getGeckolibData(DataTickets.ENTITY_BODY_YAW);

		if (renderState.getGeckolibData(DataTickets.IS_SHAKING))
			rotationYaw += (float)(Math.cos(renderState.ageInTicks * 3.25d) * Math.PI * 0.4d);

		boolean sleeping = renderState.getGeckolibData(DataTickets.ENTITY_POSE) == Pose.SLEEPING;

		if (!sleeping)
			poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));

		if (renderState instanceof LivingEntityRenderState livingRenderState) {
			if (livingRenderState.deathTime > 0) {
				poseStack.mulPose(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt((livingRenderState.deathTime - 1f) / 20f * 1.6f), 1) * getDeathMaxRotation(renderState)));
			}
			else if (livingRenderState.isAutoSpinAttack) {
				poseStack.mulPose(Axis.XP.rotationDegrees(-90f - livingRenderState.xRot));
				poseStack.mulPose(Axis.YP.rotationDegrees(renderState.ageInTicks * -75f));
			}
			else if (sleeping) {
				Direction bedOrientation = livingRenderState.bedOrientation;

				poseStack.mulPose(Axis.YP.rotationDegrees(bedOrientation != null ? RenderUtil.getDirectionAngle(bedOrientation) : rotationYaw));
				poseStack.mulPose(Axis.ZP.rotationDegrees(getDeathMaxRotation(renderState)));
				poseStack.mulPose(Axis.YP.rotationDegrees(270f));
			}
			else if (livingRenderState.isUpsideDown) {
				poseStack.translate(0, (livingRenderState.boundingBoxHeight + 0.1f) / nativeScale, 0);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
			}
		}
	}

	/**
	 * Returns the max rotation value for dying entities
	 * <p>
	 * You might want to modify this for different aesthetics, such as a {@link net.minecraft.world.entity.monster.Spider} flipping upside down on death
	 * <p>
	 * Functionally equivalent to {@code LivingEntityRenderer#getFlipDegrees}
	 */
	protected float getDeathMaxRotation(GeoRenderState renderState) {
		return 90f;
	}

	/**
	 * GeckoLib defers creation of this to allow for dynamic handling in {@link #extractRenderState(Entity, EntityRenderState, float)}
	 */
	@Deprecated
	@ApiStatus.Internal
	@Nullable
	@Override
	public EntityRenderState createRenderState() {
		return null;
	}

	/**
	 * Create the contextually relevant EntityRenderState for the current render pass
	 * <p>
	 * GeckoLib also uses this to dynamically handle the default EntityRenderState setup
	 * <p>
	 * If overriding this for a custom RenderState, ensure you call {@code super} first
	 */
	@ApiStatus.Internal
	@Override
	public final EntityRenderState createRenderState(T entity, float partialTick) {
		if (this.reusedState == null)
			this.reusedState = createBaseRenderState(entity);

		return super.createRenderState(entity, partialTick);
	}

	/**
	 * Create the base (blank) {@link R renderState} instance for this renderer.
	 * <p>
	 * By default, it is an {@link EntityRenderState}, or a {@link LivingEntityRenderState} if the entity is an instance of {@link LivingEntity}<br>
	 * All EntityRenderStates of any kind are automatically {@link GeoRenderState}s
	 * <p>
	 * Override this if you want to utilise a different subclass of EntityRenderState
	 */
	protected R createBaseRenderState(T entity) {
		return (R)(entity instanceof LivingEntity ? new LivingEntityRenderState() : new EntityRenderState());
	}

	/**
	 * Fill the EntityRenderState for the current render pass.
	 * <p>
	 * You should only be overriding this if you have extended the {@link R renderState} type.<br>
	 * If you're just adding GeckoLib rendering data, you should be using {@link #addRenderData(GeoAnimatable, Object, GeoRenderState)} instead
	 */
	@ApiStatus.OverrideOnly
	@Override
	public void extractRenderState(T entity, EntityRenderState entityRenderState, float partialTick) {
		super.extractRenderState(entity, entityRenderState, partialTick);

		if (entityRenderState instanceof LivingEntityRenderState livingEntityRenderState)
			extractLivingEntityRenderState((LivingEntity)entity, livingEntityRenderState, partialTick, this.itemModelResolver);

		fillRenderState(entity, null, (R)entityRenderState, partialTick);
	}

	/**
	 * Replica of {@link LivingEntityRenderer#extractRenderState(LivingEntity, LivingEntityRenderState, float)}.
	 * <p>
	 * This is only called if the entity for this renderer is a {@link LivingEntity}
	 */
	protected void extractLivingEntityRenderState(LivingEntity entity, LivingEntityRenderState renderState, float partialTick, ItemModelResolver itemModelResolver) {
		final Minecraft minecraft = Minecraft.getInstance();
		final float yHeadRot = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
		final ItemStack helmetStack = entity.getItemBySlot(EquipmentSlot.HEAD);

		renderState.bodyRot = LivingEntityRenderer.solveBodyRot(entity, yHeadRot, partialTick);
		renderState.yRot = Mth.wrapDegrees(yHeadRot - renderState.bodyRot);
		renderState.xRot = entity.getXRot(partialTick);
		renderState.customName = entity.getCustomName();
		renderState.isUpsideDown = LivingEntityRenderer.isEntityUpsideDown(entity);

		if (renderState.isUpsideDown) {
			renderState.xRot *= -1;
			renderState.yRot *= -1;
		}

		if (!entity.isPassenger() && entity.isAlive()) {
			renderState.walkAnimationPos = entity.walkAnimation.position(partialTick);
			renderState.walkAnimationSpeed = entity.walkAnimation.speed(partialTick);
		}
		else {
			renderState.walkAnimationPos = 0;
			renderState.walkAnimationSpeed = 0;
		}

		if (entity.getVehicle() instanceof LivingEntity vehicle) {
			renderState.wornHeadAnimationPos = vehicle.walkAnimation.position(partialTick);
		}
		else {
			renderState.wornHeadAnimationPos = renderState.walkAnimationPos;
		}

		renderState.scale = entity.getScale();
		renderState.ageScale = entity.getAgeScale();
		renderState.pose = entity.getPose();
		renderState.bedOrientation = entity.getBedOrientation();

		if (renderState.bedOrientation != null)
			renderState.eyeHeight = entity.getEyeHeight(Pose.STANDING);

		renderState.isFullyFrozen = entity.isFullyFrozen();
		renderState.isBaby = entity.isBaby();
		renderState.isInWater = entity.isInWater();
		renderState.isAutoSpinAttack = entity.isAutoSpinAttack();
		renderState.hasRedOverlay = entity.hurtTime > 0 || entity.deathTime > 0;

		if (helmetStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractSkullBlock skullBlock) {
			renderState.wornHeadType = skullBlock.getType();
			renderState.wornHeadProfile = helmetStack.get(DataComponents.PROFILE);
			renderState.headItem.clear();
		}
		else {
			renderState.wornHeadType = null;
			renderState.wornHeadProfile = null;

			if (!HumanoidArmorLayer.shouldRender(helmetStack, EquipmentSlot.HEAD)) {
				itemModelResolver.updateForLiving(renderState.headItem, helmetStack, ItemDisplayContext.HEAD, entity);
			}
			else {
				renderState.headItem.clear();
			}
		}

		renderState.deathTime = entity.deathTime > 0 ? (float)entity.deathTime + partialTick : 0;
		renderState.isInvisibleToPlayer = renderState.isInvisible && entity.isInvisibleTo(ClientUtil.getClientPlayer());
		renderState.appearsGlowing = minecraft.shouldEntityAppearGlowing(entity);
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileEntityRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderStateEvent(T animatable, Void relatedObject, R renderState) {
		GeckoLibServices.Client.EVENTS.fireCompileEntityRenderState(this, renderState, animatable);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
		return GeckoLibServices.Client.EVENTS.fireEntityPreRender(this, renderState, poseStack, model, bufferSource);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
		GeckoLibServices.Client.EVENTS.fireEntityPostRender(this, renderState, poseStack, model, bufferSource);
	}

	/**
	 * Makes a covariant variable of the given {@link GeoRenderState} and {@link LivingEntityRenderState}
	 * (Essentially a variable that is <b>both</b> types) for ease of use
	 * <p>
	 * Because of the lack of extensibility in covariant return types, a new version of this method needs to be made
	 * for any other covariant combination
	 *
	 * @param renderState The base GeoRenderState to cast
	 * @return The GeoRenderState cast <i>additively</i> as a LivingEntityRenderState
	 */
	protected <S extends LivingEntityRenderState & GeoRenderState> S convertRenderStateToLiving(R renderState) {
		return (S)renderState;
	}
}
