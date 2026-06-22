package software.bernie.geckolib.renderer.layer.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.lang.ref.WeakReference;
import java.util.function.Function;

/**
 * Generified {@link RenderLayer} implementation for rendering {@link GeoAnimatable} instances on a non-GeckoLib model
 * <p>
 * This layer handles the boilerplate for performing the rendering and optional animations but expects the user
 * to handle the specifics of positioning and typing
 */
public abstract class AttachedAnimatableRenderLayer<A extends GeoAnimatable, T extends Entity, M extends EntityModel<T>, R extends GeoRenderer<A>> extends RenderLayer<T, M> {
    private final Function<@Nullable Level, @Nullable A> instanceCache = new Function<@Nullable Level, A>() {
        private @Nullable WeakReference<A> cachedInstance = null;
        
        @Override
        @Contract("null->null;!null->!null")
        public @Nullable A apply(@Nullable Level level) {
            if (level == null) {
                this.cachedInstance = null;
                
                return null;
            }
            
            A instance;
            
            if (this.cachedInstance == null || (instance = this.cachedInstance.get()) == null)
                this.cachedInstance = new WeakReference<>(instance = AttachedAnimatableRenderLayer.this.instanceFactory.apply(level));
            
            return instance;
        }
    };
    private final Function<Level, A> instanceFactory;
    
    /**
     * Create a new {@link RenderLayer} instance
     *
     * @param renderer The vanilla renderer instance that the layer is being added to
     * @param instanceFactory A factory that creates a new GeoAnimatable instance for rendering
     */
    public AttachedAnimatableRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
        super(renderer);
        
        this.instanceFactory = instanceFactory;
    }
    
    /**
     * Perform the actual render operation for rendering the {@link GeoAnimatable} on the target entity's model
     * <p>
     * Handle any model attachment translations or rotations here<br/>
     * E.G.
     * <pre>
     *    {@code
     *      final ModelPart head = model.getHead();
     *
     *      poseStack.mulPose(Axis.YP.rotation(head.yRot));
     *      poseStack.mulPose(Axis.XP.rotation(head.xRot));
     *      poseStack.mulPose(Axis.ZP.rotation(head.zRot));
     *
     *      renderer.render(animatable, 0, partialTick, poseStack, bufferSource, packedLight);
     *    }
     * </pre>
     */
    protected abstract void renderAnimatableOnModel(T entity, A animatable, M model, R renderer, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, float ageInTicks,
                                                    float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch);
    
    /**
     * Get the {@link GeoRenderer} instance for this {@link GeoAnimatable}
     */
    protected abstract @Nullable R getRenderer(A animatable);
    
    /**
     * Determine whether this {@link RenderLayer} should render at the current time
     */
    protected boolean shouldRender(T entity) {
        return true;
    }
    
    /**
     * Get the {@link GeoAnimatable} instance for rendering
     */
    public @Nullable A getAnimatableInstance(T entity) {
        final A animatable = this.instanceCache.apply(entity.level());
        
        if (animatable != null)
            updateAnimatableTick(animatable, entity.tickCount);
        
        return animatable;
    }
    
    /**
     * Set the tick for the {@link GeoAnimatable} instance to be rendered
     * <p>
     * This should be set every render pass immediately before rendering it
     */
    protected void updateAnimatableTick(A instance, int tick) {
        if (instance instanceof Entity geoEntity)
            geoEntity.tickCount = tick;
    }
    
    /**
     * @see AttachedAnimatableRenderLayer#renderAnimatableOnModel
     */
    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!shouldRender(entity))
            return;
        
        final A animatable = getAnimatableInstance(entity);
        final R renderer;
        
        if (animatable == null || (renderer = getRenderer(animatable)) == null)
            return;
        
        poseStack.pushPose();
        renderAnimatableOnModel(entity, animatable, getParentModel(), renderer, poseStack, bufferSource, partialTick, packedLight, ageInTicks, limbSwing, limbSwingAmount, netHeadYaw, headPitch);
        poseStack.popPose();
    }
}