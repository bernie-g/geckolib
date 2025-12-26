package software.bernie.geckolib.renderer.specialty;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.Set;

/**
 * A shallow-level dyeable armour renderer for GeckoLib armor models
 * <p>
 * This approach avoids needing to change the JSON object format to natively support dyes, which is a whole can of worms
 */
public abstract class DyeableGeoArmorRenderer<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<T, R> {
    protected final Set<GeoBone> dyeableBones = new ReferenceOpenHashSet<>();
    protected @Nullable BakedGeoModel lastModel = null;

    public DyeableGeoArmorRenderer(GeoModel<T> model) {
        super(model);
    }

    /**
     * Whether the given GeoBone should be considered dyeable or not
     * <p>
     * Note that values returned from here are cached for the last rendered {@link BakedGeoModel} and require a manual reset if you intend to change these results
     *
     * @return whether the bone should be dyed or not
     */
    protected abstract boolean isBoneDyeable(GeoBone bone);

    /**
     * Override the dye color for the given bone. By default, it returns the dye color from the item itself
     * <p>
     * Only bones that were marked as 'dyeable' in {@link #isBoneDyeable(GeoBone)} are provided here
     */
    protected int getColorForBone(R renderState, GeoBone bone, int baseColour) {
        return baseColour;
    }

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
    /**
     * Gets a tint-applying color to render the given animatable with
     * <p>
     * Returns opaque white by default, multiplied by any inherent vanilla item dye color
     */
    @Override
    public int getRenderColor(T animatable, RenderData stackAndSlot, float partialTick) {
        return 0xFFFFFFFF;
    }

    /**
     * Called at the start of the render compilation pass. PoseState manipulations have not yet taken place and typically should not be made here.
     * <p>
     * Use this method to handle any preparation or pre-work required for the render submission.
     * <p>
     * Manipulation of the model's bones is not permitted here
     */
    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);

        checkBoneDyeCache(renderPassInfo);

        for (GeoBone bone : this.dyeableBones) {
            renderPassInfo.addPerBoneRender(bone, (renderPassInfo1, bone1, renderTasks1) -> {
                final R renderState = renderPassInfo1.renderState();
                final int renderColor = renderPassInfo1.renderColor();
                final RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));

                if (renderType != null) {
                    renderTasks1.submitCustomGeometry(renderPassInfo1.poseStack(), renderType, (pose, vertexConsumer) ->
                            renderDyedBone(renderState, pose, bone1, renderPassInfo1, vertexConsumer, renderColor));
                }
            });
        }
    }

    /**
     * Render a specific {@link GeoBone} with the given dye tint
     */
    protected void renderDyedBone(R renderState, PoseStack.Pose pose, GeoBone bone1, RenderPassInfo<R> renderPassInfo1, VertexConsumer vertexConsumer, int renderColor) {
        final PoseStack poseStack = new PoseStack();

        poseStack.last().set(pose);
        bone1.render(renderPassInfo1, new PoseStack(), vertexConsumer, renderPassInfo1.packedLight(), renderPassInfo1.packedOverlay(),
                     ARGB.multiply(renderColor, getColorForBone(renderState, bone1, renderState.getOrDefaultGeckolibData(DataTickets.RENDER_COLOR, -1))));
    }

    /**
     * Perform any necessary adjustments of the model here, such as positioning/scaling/rotating or hiding bones.
     * <p>
     * No manipulation of the RenderState is permitted here
     */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        for (GeoBone bone : this.dyeableBones) {
            snapshots.get(bone).skipRender(true);
        }
    }

    /**
     * Check whether the dye cache should be considered dirty and recomputed
     * <p>
     * The less this forces re-computation, the better for performance
     */
    protected void checkBoneDyeCache(RenderPassInfo<R> renderPassInfo) {
        final BakedGeoModel model = renderPassInfo.model();

        if (model != this.lastModel) {
            this.lastModel = model;

            this.dyeableBones.clear();
            collectDyeableBones(model.topLevelBones());
        }
    }

    /**
     * Recursively parse through the given bones collection, collecting and caching dyeable bones as applicable
     */
    protected void collectDyeableBones(GeoBone[] bones) {
        for (GeoBone bone : bones) {
            if (isBoneDyeable(bone))
                this.dyeableBones.add(bone);

            collectDyeableBones(bone.children());
        }
    }
    //</editor-fold>
}
