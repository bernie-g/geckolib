package com.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.layer.GeoRenderLayer;

/// A functional interface for submitting a render operation at the [pose][PoseStack.Pose] of a bone.
///
/// Typically you would submit an instance of this to [GeoRenderLayer#addPerBoneRender]
///
/// @param <R> RenderState class type
@FunctionalInterface
public interface PerBoneRender<R extends GeoRenderState> {
    /// Submit your per-bone render task for this render pass
    ///
    /// @param renderPassInfo The collated render-related data for this render pass. The contained PoseStack is pre-posed at your bone's pose
    /// @param bone The GeoBone to submit the task for
    /// @param renderTasks The render submission collector for this render pass. Submit your render task to this
    void submitRenderTask(RenderPassInfo<R> renderPassInfo, GeoBone bone, SubmitNodeCollector renderTasks);
}