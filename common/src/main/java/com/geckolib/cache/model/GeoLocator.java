package com.geckolib.cache.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.util.RenderUtil;

/// Implementation of locator markers for [GeoBone]s
///
/// These are non-rendering node elements, typically used for positioning
public class GeoLocator {
    protected GeoBone parent;
    protected String name;
    
    protected float offsetX;
    protected float offsetY;
    protected float offsetZ;
    
    protected float rotX;
    protected float rotY;
    protected float rotZ;

    @ApiStatus.Internal
    public RenderPassInfo.BonePositionListener @Nullable [] positionListeners = null;
    
    public GeoLocator(GeoBone parent, String name, float offsetX, float offsetY, float offsetZ, float rotX, float rotY, float rotZ) {
        this.parent = parent;
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    /// The parent bone of this locator
    public GeoBone parent() {
        return this.parent;
    }

    /// The name of this locator, as defined in the model JSON
    public String name() {
        return this.name;
    }

    /// The offset x coordinate, relative to its parent bone
    public float offsetX() {
        return this.offsetX;
    }

    /// The offset y coordinate, relative to its parent bone
    public float offsetY() {
        return this.offsetY;
    }

    /// The offset z coordinate, relative to its parent bone
    public float offsetZ() {
        return this.offsetZ;
    }

    /// The x rotation of this locator, in radians, relative to its parent bone
    public float rotX() {
        return this.rotX;
    }

    /// The x rotation of this locator, in radians, relative to its parent bone
    public float rotY() {
        return this.rotY;
    }

    /// The x rotation of this locator, in radians, relative to its parent bone
    public float rotZ() {
        return this.rotZ;
    }

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">

    /// Pass the current render position to any applied [RenderPassInfo.BonePositionListener]s
    @ApiStatus.Internal
    public void updatePositionListeners(PoseStack poseStack, RenderPassInfo<?> renderPassInfo) {
        if (this.positionListeners != null) {
            poseStack.pushPose();
            this.parent.translateAwayFromPivotPoint(poseStack);
            poseStack.translate(this.offsetX / 16f, this.offsetY / 16f, this.offsetZ / 16f);

            if (this.rotZ != 0)
                poseStack.mulPose(Axis.ZP.rotation(this.rotZ));

            if (this.rotY != 0)
                poseStack.mulPose(Axis.YP.rotation(this.rotY));

            if (this.rotX != 0)
                poseStack.mulPose(Axis.XP.rotation(this.rotX));

            RenderUtil.providePositionsToListeners(poseStack, renderPassInfo, this.positionListeners);

            poseStack.popPose();
        }
    }
    //</editor-fold>
}
