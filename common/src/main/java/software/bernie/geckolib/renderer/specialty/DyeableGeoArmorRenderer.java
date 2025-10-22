package software.bernie.geckolib.renderer.specialty;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Collection;
import java.util.Set;

/**
 * A shallow-level dyeable armour renderer for GeckoLib armor models
 * <p>
 * This approach avoids needing to change the JSON object format to natively support dyes, which is a whole can of worms
 */
public abstract class DyeableGeoArmorRenderer<T extends Item & GeoItem, R extends AvatarRenderState & GeoRenderState> extends GeoArmorRenderer<T, R> {
    protected final Set<GeoBone> dyeableBones = new ReferenceOpenHashSet<>();
    protected BakedGeoModel lastModel = null;

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
    protected int getColorForBone(R renderState, GeoBone bone, int packedLight, int packedOverlay, int baseColour) {
        return baseColour;
    }

    @Override
    public int getRenderColor(T animatable, RenderData stackAndSlot, float partialTick) {
        return 0xFFFFFFFF;
    }

    @Override
    public void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor) {
        super.preRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);

        checkBoneDyeCache(renderState, model, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void renderCubesOfBone(R renderState, GeoBone bone, PoseStack poseStack, VertexConsumer buffer, CameraRenderState cameraState,
                                  int packedLight, int packedOverlay, int renderColor) {
        if (this.dyeableBones.contains(bone))
            renderColor = ARGB.multiply(renderColor, getColorForBone(renderState, bone, packedLight, packedOverlay, renderState.getGeckolibData(DataTickets.RENDER_COLOR)));

        super.renderCubesOfBone(renderState, bone, poseStack, buffer, cameraState, packedLight, packedOverlay, renderColor);
    }

    /**
     * Check whether the dye cache should be considered dirty and recomputed
     * <p>
     * The less this forces re-computation, the better for performance
     */
    protected void checkBoneDyeCache(GeoRenderState renderState, BakedGeoModel model, int packedLight, int packedOverlay, int renderColor) {
        if (model != this.lastModel) {
            this.lastModel = model;

            this.dyeableBones.clear();
            collectDyeableBones(model.topLevelBones());
        }
    }

    /**
     * Recursively parse through the given bones collection, collecting and caching dyeable bones as applicable
     */
    protected void collectDyeableBones(Collection<GeoBone> bones) {
        for (GeoBone bone : bones) {
            if (isBoneDyeable(bone))
                this.dyeableBones.add(bone);

            collectDyeableBones(bone.getChildBones());
        }
    }
}
