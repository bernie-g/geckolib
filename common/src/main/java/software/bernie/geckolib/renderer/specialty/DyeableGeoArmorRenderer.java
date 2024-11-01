package software.bernie.geckolib.renderer.specialty;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.object.Color;

import java.util.Collection;
import java.util.Set;

/**
 * A shallow-level dyeable armour renderer for GeckoLib armor models
 * <p>
 * This approach avoids needing to change the JSON object format to natively support dyes, which is a whole can of worms
 */
public abstract class DyeableGeoArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T> {
    protected final Set<GeoBone> dyeableBones = new ObjectArraySet<>();
    protected BakedGeoModel lastModel = null;

    public DyeableGeoArmorRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);

        if (!isReRender)
            checkBoneDyeCache(animatable, model, partialTick, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        if (this.dyeableBones.contains(bone))
            renderColor = ARGB.multiply(renderColor, getColorForBone(bone).argbInt());

        super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, renderColor);
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
     * What color the given GeoBone should be dyed as
     * <p>
     * Only bones that were marked as 'dyeable' in {@link DyeableGeoArmorRenderer#isBoneDyeable(GeoBone)} are provided here
     */
    @NotNull
    protected abstract Color getColorForBone(GeoBone bone);

    /**
     * Check whether the dye cache should be considered dirty and recomputed
     * <p>
     * The less this forces re-computation, the better for performance
     */
    protected void checkBoneDyeCache(T animatable, BakedGeoModel model, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        if (model != this.lastModel) {
            this.dyeableBones.clear();
            this.lastModel = model;
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
