package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.scores.Team;
import org.apache.commons.lang3.mutable.MutableObject;
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
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.function.Function;

/**
 * An alternate to {@link GeoEntityRenderer}, used specifically for replacing existing non-geckolib
 * entities with geckolib rendering dynamically, without the need for an additional entity class
 */
public class GeoReplacedEntityRenderer<T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> extends EntityRenderer<E, R> implements GeoRenderer<T, E, R> {
	protected final GeoRenderLayersContainer<T, E, R> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;
	protected final ItemModelResolver itemModelResolver;
	protected final T animatable;

	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	public GeoReplacedEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model, T animatable) {
		super(context);

		this.model = model;
		this.itemModelResolver = context.getItemModelResolver();
		this.animatable = animatable;

		if (this.animatable instanceof Entity)
			throw new IllegalArgumentException("Direct entity instances are not permitted for GeoReplacedEntityRenderer animatables! Extract the GeoAnimatable from the Entity instead.");
	}

	/**
	 * Return the cached {@link GeoAnimatable} instance for this renderer
	 */
	public T getAnimatable() {
		return this.animatable;
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
	public List<GeoRenderLayer<T, E, R>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoReplacedEntityRenderer<T, E, R> withRenderLayer(Function<? super GeoReplacedEntityRenderer<T, E, R>, GeoRenderLayer<T, E, R>> renderLayer) {
        return withRenderLayer(renderLayer.apply(this));
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public GeoReplacedEntityRenderer<T, E, R> withRenderLayer(GeoRenderLayer<T, E, R> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoReplacedEntityRenderer<T, E, R> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoReplacedEntityRenderer<T, E, R> withScale(float scaleWidth, float scaleHeight) {
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
	 * @param replacedEntity An object related to the render pass or null if not applicable.
	 *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 */
	@ApiStatus.Internal
	@Override
	public long getInstanceId(T animatable, E replacedEntity) {
		return replacedEntity.getId();
	}

	/**
	 * Gets a tint-applying color to render the given animatable with
	 * <p>
	 * Returns opaque white by default, modified for invisibility in spectator
	 */
	@Override
	public int getRenderColor(T animatable, E replacedEntity, float partialTick) {
		int color = GeoRenderer.super.getRenderColor(animatable, replacedEntity, partialTick);

		if (replacedEntity.isInvisible() && !replacedEntity.isInvisibleTo(ClientUtil.getClientPlayer()))
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
	public int getPackedOverlay(T animatable, E replacedEntity, float u, float partialTick) {
		if (!(replacedEntity instanceof LivingEntity entity))
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
	public double getNameRenderCutoffDistance(E entity) {
		return 32d;
	}

	/**
	 * Whether the entity's nametag should be rendered or not
	 * <p>
	 * Used to determine nametag attachment in {@link EntityRenderer#extractRenderState(Entity, EntityRenderState, float)}
	 */
	@Override
	public boolean shouldShowName(E entity, double distToCameraSq) {
		if (!(entity instanceof LivingEntity))
			return super.shouldShowName(entity, distToCameraSq);

		if (entity.isDiscrete()) {
			double nameRenderCutoff = getNameRenderCutoffDistance(entity);

			if (distToCameraSq >= nameRenderCutoff * nameRenderCutoff)
				return false;
		}

		if (entity instanceof Mob && (!entity.shouldShowName() && (!entity.hasCustomName() || entity != this.entityRenderDispatcher.crosshairPickEntity)))
			return false;

		final Minecraft minecraft = Minecraft.getInstance();
		boolean visibleToClient = !entity.isInvisibleTo(ClientUtil.getClientPlayer());
		Team entityTeam = entity.getTeam();

		if (entityTeam == null)
			return Minecraft.renderNames() && entity != minecraft.getCameraEntity() && visibleToClient && !entity.isVehicle();

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
	 * Do that in {@link #addRenderData(GeoAnimatable, Object, GeoRenderState, float)} instead
	 */
	protected final float calculateYRot(E entity, float yHeadRot, float partialTick) {
		if (!(entity.getVehicle() instanceof LivingEntity vehicle))
			return entity.getVisualRotationYInDegrees();

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

		return renderState.appearsGlowing() ? RenderType.outline(texture) : null;
	}

	/**
	 * Internal method for capturing the common RenderState data for all animatable objects
	 */
	@ApiStatus.Internal
	@Override
	public final R captureDefaultRenderState(T animatable, E replacedEntity, R renderState, float partialTick) {
		GeoRenderer.super.captureDefaultRenderState(animatable, replacedEntity, renderState, partialTick);

		LivingEntityRenderState livingRenderState = renderState instanceof LivingEntityRenderState state ? state : null;

		renderState.addGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, livingRenderState != null ? livingRenderState.isInvisibleToPlayer : replacedEntity.isInvisible() && replacedEntity.isInvisibleTo(ClientUtil.getClientPlayer()));
		renderState.addGeckolibData(DataTickets.IS_SHAKING, livingRenderState != null ? livingRenderState.isFullyFrozen : replacedEntity.isFullyFrozen());
		renderState.addGeckolibData(DataTickets.ENTITY_POSE, livingRenderState != null ? livingRenderState.pose : replacedEntity.getPose());
		renderState.addGeckolibData(DataTickets.ENTITY_PITCH, livingRenderState != null ? livingRenderState.xRot : replacedEntity.getXRot(partialTick));
		renderState.addGeckolibData(DataTickets.ENTITY_YAW, livingRenderState != null ? livingRenderState.yRot : calculateYRot(replacedEntity, 0, partialTick));
		renderState.addGeckolibData(DataTickets.ENTITY_BODY_YAW, livingRenderState != null ? livingRenderState.bodyRot : renderState.getGeckolibData(DataTickets.ENTITY_YAW));
		renderState.addGeckolibData(DataTickets.VELOCITY, replacedEntity.getDeltaMovement());
		renderState.addGeckolibData(DataTickets.BLOCKPOS, replacedEntity.blockPosition());
		renderState.addGeckolibData(DataTickets.SPRINTING, replacedEntity.isSprinting());
		renderState.addGeckolibData(DataTickets.IS_MOVING, (replacedEntity instanceof LivingEntity livingEntity ? livingEntity.walkAnimation.speed() : replacedEntity.getDeltaMovement().lengthSqr())  >= getMotionAnimThreshold(this.animatable));

		if (replacedEntity instanceof LivingEntity livingEntity) {
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
    public void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor) {
        renderState.addGeckolibData(DataTickets.OBJECT_RENDER_POSE, new Matrix4f(poseStack.last().pose()));
	}

    /**
     * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
     * <p>
     * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
     */
    @Override
    public void scaleModelForRender(R renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
        float nativeScale = renderState instanceof LivingEntityRenderState livingRenderState ? livingRenderState.scale : 1;

        GeoRenderer.super.scaleModelForRender(renderState, widthScale * this.scaleWidth * nativeScale, heightScale * this.scaleHeight * nativeScale, poseStack, model, cameraState);
    }

	/**
	 * Transform the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
	 */
	@Override
    public void adjustRenderPose(R renderState, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
		LivingEntityRenderState livingRenderState = renderState instanceof LivingEntityRenderState state ? state : null;

		if (livingRenderState != null && renderState.getGeckolibData(DataTickets.ENTITY_POSE) == Pose.SLEEPING) {
			Direction bedDirection = livingRenderState.bedOrientation;

			if (bedDirection != null) {
				float eyePosOffset = livingRenderState.eyeHeight - 0.1F;

				poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
			}
		}

        applyRotations(renderState, poseStack, renderState instanceof LivingEntityRenderState state ? state.scale : 1);
        poseStack.translate(0, 0.01f, 0);
        renderState.addGeckolibData(DataTickets.MODEL_RENDER_POSE, new Matrix4f(poseStack.last().pose()));
	}

    /**
     * Vanilla entrypoint for rendering.
     * <p>
     * You generally shouldn't need to override or use this method.
     */
    @ApiStatus.Internal
    @Override
    public void submit(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        submitRenderTasks(renderState, poseStack, renderTasks, cameraState);
    }

    /**
     * Called after all other rendering work has taken place, including reverting the {@link PoseStack}'s state
     */
    @Override
    public void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                            int packedLight, int packedOverlay, int renderColor) {
        super.submit(renderState, poseStack, renderTasks, cameraState);
    }

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
    public void renderBone(R renderState, PoseStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState, boolean skipBoneTasks,
                           int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();
		RenderUtil.translateMatrixToBone(poseStack, bone);
		RenderUtil.translateToPivotPoint(poseStack, bone);
		RenderUtil.rotateMatrixAroundBone(poseStack, bone);
		RenderUtil.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());
			Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, renderState.getGeckolibData(DataTickets.OBJECT_RENDER_POSE));

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, renderState.getGeckolibData(DataTickets.MODEL_RENDER_POSE)));
			bone.setLocalSpaceMatrix(RenderUtil.translateMatrix(localMatrix, getRenderOffset(renderState).toVector3f()));
			bone.setWorldSpaceMatrix(RenderUtil.translateMatrix(new Matrix4f(localMatrix), new Vector3f((float)renderState.x, (float)renderState.y, (float)renderState.z)));
		}

		RenderUtil.translateAwayFromPivotPoint(poseStack, bone);

		if (!skipBoneTasks) {
			Pair<MutableObject<PoseStack.Pose>, PerBoneRender<R>> boneRenderTask = getPerBoneTasks(renderState).get(bone);

			if (boneRenderTask != null)
				boneRenderTask.left().setValue(poseStack.last().copy());
		}

        renderCubesOfBone(renderState, bone, poseStack, buffer, cameraState, packedLight, packedOverlay, renderColor);
        renderChildBones(renderState, bone, poseStack, buffer, cameraState, skipBoneTasks, packedLight, packedOverlay, renderColor);
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
     * Create the base (blank) {@link R renderState} instance for this renderer.
     * <p>
     * By default, it is an {@link EntityRenderState}, or a {@link LivingEntityRenderState} if the entity is an instance of {@link LivingEntity}<br>
     * All EntityRenderStates of any kind are automatically {@link GeoRenderState}s
     * <p>
     * Override this if you want to utilise a different subclass of EntityRenderState
     */
    @Override
    public R createRenderState(T animatable, E relatedObject) {
        return (R)(relatedObject instanceof LivingEntity ? new LivingEntityRenderState() : new EntityRenderState());
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
	public final R createRenderState(E entity, float partialTick) {
        R renderState = createRenderState(this.animatable, entity);

        extractRenderState(entity, renderState, partialTick);
        finalizeRenderState(entity, renderState);

        return renderState;
	}

	/**
	 * Fill the EntityRenderState for the current render pass.
	 * <p>
	 * You should only be overriding this if you have extended the {@link R renderState} type.<br>
	 * If you're just adding GeckoLib rendering data, you should be using {@link #addRenderData(GeoAnimatable, Object, GeoRenderState, float)} instead
	 */
	@ApiStatus.Internal
	@Override
	public void extractRenderState(E entity, R renderState, float partialTick) {
		super.extractRenderState(entity, renderState, partialTick);

		if (renderState instanceof LivingEntityRenderState livingEntityRenderState)
			extractLivingEntityRenderState((LivingEntity)entity, livingEntityRenderState, partialTick, this.itemModelResolver);

		fillRenderState(this.animatable, entity, renderState, partialTick);
	}

	/**
	 * Replica of {@link LivingEntityRenderer#extractRenderState(LivingEntity, LivingEntityRenderState, float)}.
	 * <p>
	 * This is only called if the entity for this renderer is a {@link LivingEntity}
	 */
	protected void extractLivingEntityRenderState(LivingEntity entity, LivingEntityRenderState renderState, float partialTick, ItemModelResolver itemModelResolver) {
		final float lerpHeadYRot = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        final Component customName = entity.getCustomName();
		final ItemStack helmetStack = entity.getItemBySlot(EquipmentSlot.HEAD);

		renderState.bodyRot = LivingEntityRenderer.solveBodyRot(entity, lerpHeadYRot, partialTick);
		renderState.yRot = Mth.wrapDegrees(lerpHeadYRot - renderState.bodyRot);
		renderState.xRot = entity.getXRot(partialTick);
        renderState.isUpsideDown = customName != null && LivingEntityRenderer.isUpsideDownName(customName.getString());

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
                this.itemModelResolver.updateForLiving(renderState.headItem, helmetStack, ItemDisplayContext.HEAD, entity);
			}
			else {
				renderState.headItem.clear();
			}
		}

		renderState.deathTime = entity.deathTime > 0 ? (float)entity.deathTime + partialTick : 0;
		renderState.isInvisibleToPlayer = renderState.isInvisible && entity.isInvisibleTo(ClientUtil.getClientPlayer());
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileReplacedEntityRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderStateEvent(T animatable, E entity, R renderState, float partialTick) {
		GeckoLibServices.Client.EVENTS.fireCompileReplacedEntityRenderState(this, renderState, animatable, entity);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 * <p>
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
		return GeckoLibServices.Client.EVENTS.fireReplacedEntityPreRender(this, renderState, poseStack, model, renderTasks, cameraState);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
		GeckoLibServices.Client.EVENTS.fireReplacedEntityPostRender(this, renderState, poseStack, model, renderTasks, cameraState);
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
	protected <S extends LivingEntityRenderState & GeoRenderState> S makeLivingCovariantRenderState(GeoRenderState renderState) {
		return (S)renderState;
	}

    /**
     * @deprecated GeckoLib defers creation of this to allow for dynamic handling in {@link #extractRenderState(Entity, EntityRenderState, float)}
     */
    @Deprecated
    @ApiStatus.Internal
    @Nullable
    @Override
    public R createRenderState() {
        return null;
    }
}
