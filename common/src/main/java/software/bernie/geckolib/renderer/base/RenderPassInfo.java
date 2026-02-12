package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.cache.model.GeoLocator;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.object.DeferredCache;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;
import java.util.Map;

/// Container class holding all the common information relevant for a single render pass in GeckoLib.
///
/// [GeoRenderer] builds an instance of this at the start of a render pass, and uses it until completion, then discards it.
///
/// This allows for a significant aggregation of the various objects passed to render methods, as well as
/// allowing extensibility where it may be wanted
///
/// This should hopefully make it easier to organize and manage data for rendering for end-users
///
/// **<u>NOTE:</u>** All objects contained by this instance should be considered functionally immutable.
///
/// @param <R> RenderState class type
public class RenderPassInfo<R extends GeoRenderState> {
    protected final GeoRenderer<?, ?, R> renderer;
    protected final R renderState;
    protected final PoseStack poseStack;
    protected final BakedGeoModel model;
    protected final CameraRenderState cameraState;
    protected final boolean willRender;
    protected final Matrix4f objectRenderPose;
    protected final Matrix4f modelRenderPose;

    protected final DeferredCache<List<BoneUpdater<R>>, BoneSnapshot[]> boneUpdates = new DeferredCache<>(new ObjectArrayList<>(), this::compileBoneUpdates);
    protected final Map<GeoBone, List<PerBoneRender<R>>> boneRenderTasks = new Reference2ObjectArrayMap<>();
    protected final Map<GeoBone, List<BonePositionListener>> bonePositionListeners = new Reference2ObjectArrayMap<>();
    protected final Map<GeoLocator, List<BonePositionListener>> locatorPositionListeners = new Reference2ObjectArrayMap<>();

    /// @see #create
    /// @param renderer The GeoRenderer instance this instance is for
    /// @param renderState The RenderState instance for this render pass
    /// @param poseStack The PoseStack instance for this render pass
    /// @param model The BakedGeoModel instance for this render pass
    /// @param cameraState The CameraRenderState instance for this render pass
    /// @param willRender Whether the model should actually render in this render pass. Typically false if [GeoRenderer#getRenderType] returns null.
    ///                   This does not guarantee that the model will render, only that it should.
    protected RenderPassInfo(GeoRenderer<?, ?, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState, boolean willRender) {
        this.renderer = renderer;
        this.renderState = renderState;
        this.poseStack = poseStack;
        this.model = model;
        this.cameraState = cameraState;
        this.willRender = willRender;
        this.objectRenderPose = new Matrix4f(poseStack.last().pose());
        this.modelRenderPose = new Matrix4f();
    }

    /// @return The GeoRenderer instance this instance is for
    public GeoRenderer<?, ?, R> renderer() {
        return this.renderer;
    }

    /// @return The GeoRenderState instance for this render pass
    public R renderState() {
        return this.renderState;
    }

    /// @return The PoseStack instance for this render pass
    public PoseStack poseStack() {
        return this.poseStack;
    }

    /// @return The BakedGeoModel instance for this render pass
    public BakedGeoModel model() {
        return this.model;
    }

    /// @return The CameraRenderState instance for this render pass
    public CameraRenderState cameraState() {
        return this.cameraState;
    }

    /// @return The packed light value for this render pass
    public int packedLight() {
        return this.renderState.getPackedLight();
    }

    /// @return The packed overlay coordinates for this render pass
    public int packedOverlay() {
        return this.renderState.getOrDefaultGeckolibData(DataTickets.PACKED_OVERLAY, OverlayTexture.NO_OVERLAY);
    }

    /// @return The packed (ARGB) color/tint value for this render pass
    public int renderColor() {
    	return this.renderState.getOrDefaultGeckolibData(DataTickets.RENDER_COLOR, 0xFFFFFFFF);
    }

    /// @return Whether the model should actually render in this render pass.
    /// Typically false if [GeoRenderer#getRenderType] returns null.
    public boolean willRender() {
        return this.willRender;
    }

    /// Shortcut method for retrieving render data from the [GeoRenderState]
    ///
    /// @see #renderState()
    public <D> @Nullable D getGeckolibData(DataTicket<D> dataTicket) {
        return this.renderState().getGeckolibData(dataTicket);
    }

    /// Shortcut method for retrieving render data from the [GeoRenderState]
    ///
    /// @see #renderState()
    public <D> D getOrDefaultGeckolibData(DataTicket<D> dataTicket, D fallback) {
        return this.renderState().getOrDefaultGeckolibData(dataTicket, fallback);
    }

    /// Get the [PoseStack.Pose#pose()] for the current render pass representing
    /// the state of the PoseStack prior to any renderer-specific manipulations
    public Matrix4f getPreRenderMatrixState() {
        return this.objectRenderPose;
    }

    /// Get the [PoseStack.Pose#pose()] for the current render pass representing
    /// the state of the PoseStack immediately prior to submitting the render task
    ///
    /// **<u>NOTE:</u>** Must not be called prior to [GeoRenderer#submitRenderTasks]
    public Matrix4f getModelRenderMatrixState() {
        if ((this.modelRenderPose.properties() & Matrix4fc.PROPERTY_IDENTITY) != 0)
            throw new IllegalStateException("Attempting to access model render matrix state before it has been set");

        return this.modelRenderPose;
    }

    /// Add a [PerBoneRender] task to be executed for a specific bone in the model.
    ///
    /// Typically should only be called from [GeoRenderLayer#addPerBoneRender]
    public void addPerBoneRender(GeoBone bone, PerBoneRender<R> render) {
        this.boneRenderTasks.computeIfAbsent(bone, key -> new ObjectArrayList<>()).add(render);
    }

    /// Add a BoneUpdater for this render pass
    ///
    /// Can only be called prior to the renderer submitting this pass for rendering
    /// Updaters added after that point will be ignored
    public void addBoneUpdater(BoneUpdater<R> updater) {
        try {
            this.boneUpdates.getInput().add(updater);
        }
        catch (IllegalStateException ex) {
            GeckoLibConstants.LOGGER.error("BoneUpdater added after render pass submission", ex);
        }
    }

    /// Add a BonePositionListener for this render pass
    ///
    /// Use this to capture bone matrix positions at the time of render, which is the only time they actually have a position of any kind
    public void addBonePositionListener(String boneName, BonePositionListener listener) {
        this.model.getBone(boneName).ifPresent(bone -> addBonePositionListener(bone, listener));
    }

    /// Add a BonePositionListener for this render pass
    ///
    /// Use this to capture bone matrix positions at the time of render, which is the only time they actually have a position of any kind
    public void addBonePositionListener(GeoBone bone, BonePositionListener listener) {
        this.bonePositionListeners.computeIfAbsent(bone, key -> new ObjectArrayList<>()).add(listener);
    }

    /// Add a BonePositionListener for a [GeoLocator] for this render pass
    ///
    /// Use this to capture bone matrix positions at the time of render, which is the only time they actually have a position of any kind
    public void addLocatorPositionListener(String boneName, BonePositionListener listener) {
        this.model.getLocator(boneName)
                .ifPresent(locator -> this.locatorPositionListeners
                        .computeIfAbsent(locator, _ -> new ObjectArrayList<>())
                        .add(listener));
    }

    /// Wrap a render task, posing the model using this RenderPassInfo's bone updates
    ///
    /// All bone
    public void renderPosed(Runnable renderTask) {
        final BoneSnapshot[] updates = this.boneUpdates.compute();

        for (BoneSnapshot update : updates) {
            update.apply();
        }

        if (!this.bonePositionListeners.isEmpty()) {
            for (Map.Entry<GeoBone, List<BonePositionListener>> boneListeners : this.bonePositionListeners.entrySet()) {
                boneListeners.getKey().positionListeners = boneListeners.getValue().toArray(new BonePositionListener[0]);
            }
        }

        if (!this.locatorPositionListeners.isEmpty()) {
            for (Map.Entry<GeoLocator, List<BonePositionListener>> locatorListeners : this.locatorPositionListeners.entrySet()) {
                locatorListeners.getKey().positionListeners = locatorListeners.getValue().toArray(new BonePositionListener[0]);
            }
        }

        try {
            renderTask.run();
        }
        catch (Exception ex) {
            GeckoLibConstants.LOGGER.error("Error while rendering GeckoLib model", ex);
        }
        finally {
            for (BoneSnapshot snapshot : updates) {
                snapshot.cleanup();
            }

            if (!this.bonePositionListeners.isEmpty()) {
                for (GeoBone bone : this.bonePositionListeners.keySet()) {
                    bone.positionListeners = null;
                }
            }

            if (!this.locatorPositionListeners.isEmpty()) {
                for (GeoLocator locator : this.locatorPositionListeners.keySet()) {
                    locator.positionListeners = null;
                }
            }
        }
    }

    /// Singular GeoBone-positioning callback to run immediately before rendering a model
    ///
    /// @param <R> RenderState class type
    @FunctionalInterface
    public interface BoneUpdater<R extends GeoRenderState> {
        /// Run this BoneUpdate, adjusting one or more GeoBones for a single render pass
        ///
        /// @param renderPassInfo The collated render-related data for this render pass. GeoBone instances can be retrieved from the BakedGeoModel contained in this
        /// @param snapshots Function to retrieve a BoneSnapshot for a given bone by its name. Use this to transform a bone for this render pass
        void run(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots);
    }

    /// Functional interface for a listener of bone render positions
    @FunctionalInterface
    public interface BonePositionListener {
        void accept(@Nullable Vec3 worldPos, @Nullable Vec3 modelPos, @Nullable Vec3 localPos);
    }

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">

    /// Create a new RenderPassInfo instance
    @ApiStatus.Internal
    public static <R extends GeoRenderState> RenderPassInfo<R> create(GeoRenderer<?, ?, R> renderer, R renderState, PoseStack poseStack, CameraRenderState cameraState, boolean willRender) {
        final GeoModel<?> geoModel = renderer.getGeoModel();
        final BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(renderState));
        final RenderPassInfo<R> renderPassInfo = new RenderPassInfo<>(renderer, renderState, poseStack, model, cameraState, willRender);

        renderPassInfo.addBoneUpdater(renderer::applyAnimationControllers);
        renderPassInfo.addBoneUpdater(renderer::adjustModelBonesForRender);

        return renderPassInfo;
    }

    /// Run through [#boneUpdates] and create update snapshots for each bone
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @ApiStatus.Internal
    protected BoneSnapshot[] compileBoneUpdates(List<BoneUpdater<R>> boneUpdaters) {
        final List<BoneSnapshot> snapshots = new ObjectArrayList<>(boneUpdaters.size());

        for (BoneUpdater<R> updater : boneUpdaters) {
            updater.run(this, boneName -> this.model.getBone(boneName).map(bone -> {
                if (bone.frameSnapshot == null)
                    snapshots.add(bone.frameSnapshot = BoneSnapshot.create(bone));

                return bone.frameSnapshot;
            }));
        }

        final BoneSnapshot[] array = snapshots.toArray(new BoneSnapshot[0]);

        for (int i = 0; i < array.length; i++) {
            array[i].cleanup();
        }

        return array;
    }

    /// @return The [PerBoneRender] collection for this render pass
    @ApiStatus.Internal
    public Map<GeoBone, List<PerBoneRender<R>>> getBoneRenderTasks() {
        return this.boneRenderTasks;
    }

    /// Tell the [#poseStack] to cache its current state in the [#modelRenderPose], for re-use later
    @ApiStatus.Internal
    public void captureModelRenderPose() {
        this.modelRenderPose.set(this.poseStack.last().pose());
    }

    //</editor-fold>
}
