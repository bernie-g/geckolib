package com.geckolib.renderer;

import com.geckolib.renderer.base.GeoRendererInternals;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibClientServices;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.geckolib.renderer.layer.GeoRenderLayersContainer;
import com.geckolib.util.ClientUtil;
import com.geckolib.util.MiscUtil;

import java.util.List;
import java.util.function.Function;

/// An alternate to [GeoEntityRenderer], used specifically for replacing existing non-geckolib
/// entities with geckolib rendering dynamically, without the need for an additional entity class
///
/// @param <T> Entity animatable class type. This is the animatable being rendered
/// @param <E> Entity class type. This is the entity being replaced
/// @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
public class GeoReplacedEntityRenderer<T extends GeoAnimatable, E extends Entity, R extends EntityRenderState> extends EntityRenderer<E, R> implements GeoRenderer<T, E, R> {
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

	/// Return the cached [GeoAnimatable] instance for this renderer
	public T getAnimatable() {
		return this.animatable;
	}

	/// Get the maximum distance (in blocks) that an entity's nameplate should be visible when it is sneaking
	///
	/// This is only a short-circuit predicate, and other conditions after this check must be also passed in order for the name to render
	///
	/// This is hard-capped at a maximum of 256 blocks regardless of what this method returns
	public double getNameRenderCutoffDistance(E entity) {
		return 32d;
	}

	/// Returns the max rotation value for dying entities
	///
	/// You might want to modify this for different aesthetics, such as a [Spider] flipping upside down on death
	///
	/// Functionally equivalent to `LivingEntityRenderer#getFlipDegrees`
	protected float getDeathMaxRotation(GeoRenderState renderState) {
		return 90f;
	}

	/// Makes a covariant variable of the given [GeoRenderState] and [LivingEntityRenderState]
	/// (Essentially a variable that is **both** types) for ease of use
	///
	/// Because of the lack of extensibility in covariant return types, a new version of this method needs to be made
	/// for any other covariant combination
	///
	/// @param renderState The base GeoRenderState to cast
	/// @return The GeoRenderState cast _additively_ as a LivingEntityRenderState
	@SuppressWarnings("unchecked")
    protected <S extends LivingEntityRenderState & GeoRenderState> S convertRenderStateToLiving(GeoRenderState renderState) {
		return (S)renderState;
	}

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
    /// @deprecated GeckoLib defers creation of this to allow for dynamic handling in [#extractRenderState(Entity, EntityRenderState, float)]
    @Deprecated
    @ApiStatus.Internal
    @Override
    public @Nullable R createRenderState() {
        return null;
    }

    /// Gets the model instance for this renderer
    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

    /// Returns the list of registered [GeoRenderLayers][GeoRenderLayer] for this renderer
    @Override
    public List<GeoRenderLayer<T, E, R>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    /// Adds a [GeoRenderLayer] to this renderer, to be called after the main model is rendered each frame
    @SuppressWarnings("UnusedReturnValue")
    public GeoReplacedEntityRenderer<T, E, R> withRenderLayer(Function<? super GeoReplacedEntityRenderer<T, E, R>, GeoRenderLayer<T, E, R>> renderLayer) {
        return withRenderLayer(renderLayer.apply(this));
    }

    /// Adds a [GeoRenderLayer] to this renderer, to be called after the main model is rendered each frame
    public GeoReplacedEntityRenderer<T, E, R> withRenderLayer(GeoRenderLayer<T, E, R> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

    /// Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
    public GeoReplacedEntityRenderer<T, E, R> withScale(float scale) {
        return withScale(scale, scale);
    }

    /// Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
    public GeoReplacedEntityRenderer<T, E, R> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /// Gets the id that represents the current animatable's instance for animation purposes.
    ///
    /// @param animatable The Animatable instance being renderer
    /// @param replacedEntity An object related to the render pass or null if not applicable.
    ///                         (E.G., ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
    @ApiStatus.OverrideOnly
    @Override
    public long getInstanceId(T animatable, @SuppressWarnings("NullableProblems") @NonNull E replacedEntity) {
        return replacedEntity.getId();
    }

    /// Gets a tint-applying color to render the given animatable with
    ///
    /// Returns opaque white by default, modified for invisibility in spectator
    @Override
    public int getRenderColor(T animatable, E replacedEntity, float partialTick) {
        int color = GeoRenderer.super.getRenderColor(animatable, replacedEntity, partialTick);
        Player player = ClientUtil.getClientPlayer();

        if (replacedEntity.isInvisible() && player != null && !replacedEntity.isInvisibleTo(player))
            color = ARGB.color(Mth.ceil(ARGB.alpha(color) * 38 / 255f), color);

        return color;
    }

    /// Gets a packed overlay coordinate pair for rendering
    ///
    /// Mostly just used for the red tint when an entity is hurt,
    /// but can be used for other things like the [net.minecraft.world.entity.monster.Creeper]
    /// white tint when exploding.
    @Override
    public int getPackedOverlay(T animatable, E replacedEntity, float u, float partialTick) {
        if (!(replacedEntity instanceof LivingEntity entity))
            return OverlayTexture.NO_OVERLAY;

        return OverlayTexture.pack(OverlayTexture.u(u),
                                   OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    /// Whether the entity's nametag should be rendered or not
    ///
    /// Used to determine nametag attachment in [EntityRenderer#extractRenderState(Entity, EntityRenderState, float)]
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
        final Player player = ClientUtil.getClientPlayer();
        boolean visibleToClient = player != null && !entity.isInvisibleTo(player);
        Team entityTeam = entity.getTeam();

        if (player == null || entityTeam == null)
            return Minecraft.renderNames() && entity != minecraft.getCameraEntity() && visibleToClient && !entity.isVehicle();

        Team playerTeam = player.getTeam();

        return switch (entityTeam.getNameTagVisibility()) {
            case ALWAYS -> visibleToClient;
            case NEVER -> false;
            case HIDE_FOR_OTHER_TEAMS -> playerTeam == null ? visibleToClient : entityTeam.isAlliedTo(playerTeam) && (entityTeam.canSeeFriendlyInvisibles() || visibleToClient);
            case HIDE_FOR_OWN_TEAM -> playerTeam == null ? visibleToClient : !entityTeam.isAlliedTo(playerTeam) && visibleToClient;
        };
    }

    /// Calculate the yaw of the given animatable.
    ///
    /// Normally only called for non-[LivingEntities][LivingEntity], and shouldn't be considered a safe place to modify rotation
    /// Do that in [GeoRendererInternals#addRenderData(GeoAnimatable, Object, GeoRenderState, float)] instead
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


    /// Gets the [RenderType] to render the current render pass with
    ///
    /// Uses the [RenderTypes#entityCutout] `RenderType` by default
    ///
    /// Override this to change the way a model will render (such as translucent models, etc.)
    ///
    /// @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions from taking place
    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        if (renderState.isInvisible && !renderState.getOrDefaultGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, false))
            return RenderTypes.entityTranslucentCullItemTarget(texture);

        if (!renderState.isInvisible)
            return GeoRenderer.super.getRenderType(renderState, texture);

        return renderState.appearsGlowing() ? RenderTypes.outline(texture) : null;
    }

    /// Internal method for capturing the common RenderState data for all animatable objects
    @ApiStatus.Internal
    @Override
    public final void captureDefaultRenderState(T animatable, E replacedEntity, R renderState, float partialTick) {
        GeoRenderer.super.captureDefaultRenderState(animatable, replacedEntity, renderState, partialTick);

        LivingEntityRenderState livingRenderState = renderState instanceof LivingEntityRenderState state ? state : null;

        renderState.addGeckolibData(DataTickets.TICK, (double)renderState.ageInTicks);
        renderState.addGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, livingRenderState != null ? livingRenderState.isInvisibleToPlayer : replacedEntity.isInvisible() && (ClientUtil.getClientPlayer() == null || replacedEntity.isInvisibleTo(ClientUtil.getClientPlayer())));
        renderState.addGeckolibData(DataTickets.IS_SHAKING, livingRenderState != null ? livingRenderState.isFullyFrozen : replacedEntity.isFullyFrozen());
        renderState.addGeckolibData(DataTickets.ENTITY_POSE, livingRenderState != null ? livingRenderState.pose : replacedEntity.getPose());
        renderState.addGeckolibData(DataTickets.ENTITY_PITCH, livingRenderState != null ? livingRenderState.xRot : replacedEntity.getXRot(partialTick));
        renderState.addGeckolibData(DataTickets.ENTITY_YAW, livingRenderState != null ? livingRenderState.yRot : calculateYRot(replacedEntity, 0, partialTick));
        renderState.addGeckolibData(DataTickets.ENTITY_BODY_YAW, livingRenderState != null ? livingRenderState.bodyRot : renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f));
        renderState.addGeckolibData(DataTickets.VELOCITY, replacedEntity.getDeltaMovement());
        renderState.addGeckolibData(DataTickets.BLOCKPOS, replacedEntity.blockPosition());
        renderState.addGeckolibData(DataTickets.SPRINTING, replacedEntity.isSprinting());
        renderState.addGeckolibData(DataTickets.POSITION, replacedEntity.position());
        renderState.addGeckolibData(DataTickets.IS_MOVING, (replacedEntity instanceof LivingEntity livingEntity ? livingEntity.walkAnimation.speed() : replacedEntity.getDeltaMovement().lengthSqr())  >= getMotionAnimThreshold(this.animatable));

        if (replacedEntity instanceof LivingEntity livingEntity) {
            renderState.addGeckolibData(DataTickets.SWINGING_ARM, livingEntity.swinging);
            renderState.addGeckolibData(DataTickets.IS_DEAD_OR_DYING, livingEntity.isDeadOrDying());
        }
    }

    /// Scales the [PoseStack] in preparation for rendering the model, excluding when re-rendering the model as part of a [GeoRenderLayer] or external render call
    ///
    /// Override and call `super` with modified scale values as needed to further modify the scale of the model
    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        float nativeScale = renderPassInfo.renderState() instanceof LivingEntityRenderState livingRenderState ? livingRenderState.scale : 1;

        GeoRenderer.super.scaleModelForRender(renderPassInfo, widthScale * this.scaleWidth * nativeScale, heightScale * this.scaleHeight * nativeScale);
    }

    /// Transform the [PoseStack] in preparation for rendering the model.
    ///
    /// This is called after [#scaleModelForRender], and so any transformations here will be scaled appropriately.
    /// If you need to do pre-scale translations, use [#preRenderPass]
    ///
    /// PoseStack translations made here are kept until the end of the render process
    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        final R renderState = renderPassInfo.renderState();
        final LivingEntityRenderState livingRenderState = renderState instanceof LivingEntityRenderState state ? state : null;
        final PoseStack poseStack = renderPassInfo.poseStack();

        if (livingRenderState != null && renderState.getGeckolibData(DataTickets.ENTITY_POSE) == Pose.SLEEPING) {
            Direction bedDirection = livingRenderState.bedOrientation;

            if (bedDirection != null) {
                float eyePosOffset = livingRenderState.eyeHeight - 0.1F;

                poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
            }
        }

        applyRotations(renderPassInfo, poseStack, livingRenderState != null ? livingRenderState.scale : 1);
        poseStack.translate(0, 0.01f, 0);
    }

    /// Initial access point for vanilla's [GeoEntityRenderer] class
    /// Immediately defers to [GeoRenderer#performRenderPass(GeoRenderState, PoseStack, SubmitNodeCollector, CameraRenderState)]
    @ApiStatus.Internal
    @Override
    public void submit(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        GeoRenderer.super.performRenderPass(renderState, poseStack, renderTasks, cameraState);
    }

    /// Called after the rest of the render pass has completed, including discarding the PoseStack's pose.
    ///
    /// The actual rendering of the object has not yet taken place, as that is done in a deferred [submission][#performRenderPass]
    @Override
    public void postRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.submit(renderPassInfo.renderState(), renderPassInfo.poseStack(), renderTasks, renderPassInfo.cameraState());
    }

    /// Applies rotation transformations to the renderer prior to render time to account for various entity states
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        final R renderState = renderPassInfo.renderState();
        float rotationYaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_BODY_YAW, 0f);

        if (renderState.getOrDefaultGeckolibData(DataTickets.IS_SHAKING, false))
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

                poseStack.mulPose(Axis.YP.rotationDegrees(bedOrientation != null ? MiscUtil.getDirectionAngle(bedOrientation) : rotationYaw));
                poseStack.mulPose(Axis.ZP.rotationDegrees(getDeathMaxRotation(renderState)));
                poseStack.mulPose(Axis.YP.rotationDegrees(270f));
            }
            else if (livingRenderState.isUpsideDown) {
                poseStack.translate(0, (livingRenderState.boundingBoxHeight + 0.1f) / nativeScale, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
        }
    }

    /// Create the base (blank) [renderState][R] instance for this renderer.
    ///
    /// By default, it is an [EntityRenderState], or a [LivingEntityRenderState] if the entity is an instance of [LivingEntity]
    /// All EntityRenderStates of any kind are automatically [GeoRenderState]s
    ///
    /// Override this if you want to use a different subclass of EntityRenderState
    @SuppressWarnings("unchecked")
    @Override
    public R createRenderState(T animatable, E relatedObject) {
        return (R)(relatedObject instanceof LivingEntity ? new LivingEntityRenderState() : new EntityRenderState());
    }

    /// Create the contextually relevant EntityRenderState for the current render pass
    ///
    /// GeckoLib also uses this to dynamically handle the default EntityRenderState setup
    ///
    /// If overriding this for a custom RenderState, ensure you call `super` first
    @ApiStatus.Internal
    @Override
    public final R createRenderState(E entity, float partialTick) {
        R renderState = createRenderState(this.animatable, entity);

        extractRenderState(entity, renderState, partialTick);
        finalizeRenderState(entity, renderState);

        return renderState;
    }

    /// Fill the EntityRenderState for the current render pass.
    ///
    /// You should only be overriding this if you have extended the [renderState][R] type.
    /// If you're just adding GeckoLib rendering data, you should be using [GeoRendererInternals#addRenderData(GeoAnimatable, Object, GeoRenderState, float)] instead
    @ApiStatus.Internal
    @Override
    public void extractRenderState(E entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);

        if (renderState instanceof LivingEntityRenderState livingEntityRenderState)
            extractLivingEntityRenderState((LivingEntity)entity, livingEntityRenderState, partialTick, this.itemModelResolver);

        fillRenderState(this.animatable, entity, renderState, partialTick);
    }

    /// Replica of [LivingEntityRenderer#extractRenderState(LivingEntity, LivingEntityRenderState, float)].
    ///
    /// This is only called if the entity for this renderer is a [LivingEntity]
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
        renderState.isInvisibleToPlayer = renderState.isInvisible && (ClientUtil.getClientPlayer() == null || entity.isInvisibleTo(ClientUtil.getClientPlayer()));
    }

    /// Create and fire the relevant `CompileLayers` event hook for this renderer
    @Override
    public void fireCompileRenderLayersEvent() {
        GeckoLibClientServices.EVENTS.fireCompileReplacedEntityRenderLayers(this);
    }

    /// Create and fire the relevant `CompileRenderState` event hook for this renderer
    @Override
    public void fireCompileRenderStateEvent(T animatable, E entity, R renderState, float partialTick) {
        GeckoLibClientServices.EVENTS.fireCompileReplacedEntityRenderState(this, renderState, animatable, entity);
    }

    /// Create and fire the relevant `Pre-Render` event hook for this renderer
    ///
    /// @return Whether the renderer should proceed based on the cancellation state of the event
    @Override
    public boolean firePreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return GeckoLibClientServices.EVENTS.fireReplacedEntityPreRender(renderPassInfo, renderTasks);
    }
    //</editor-fold>
}
