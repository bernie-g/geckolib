package com.geckolib.renderer.specialty;

import com.geckolib.renderer.base.GeoRendererInternals;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

/// Specialty class for rendering directionally oriented projectiles.
///
/// Automatically handles transforms for the entity based on its current rotation.
///
/// **<u>NOTE:</u>** This renderer assumes your model is laying flat, pointing directly north
public class DirectionalProjectileRenderer<T extends Projectile & GeoAnimatable, R extends EntityRenderState> extends GeoEntityRenderer<T, R> {
    /// Creates a new defaulted renderer instance, using the entity's registered id as the file name for its assets
    public DirectionalProjectileRenderer(EntityRendererProvider.Context context, EntityType<? extends T> entityType) {
        this(context, new DefaultedEntityGeoModel<>(BuiltInRegistries.ENTITY_TYPE.getKey(entityType)));
    }

    public DirectionalProjectileRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    /// Applies rotation transformations to the renderer prior to render time to account for various entity states
    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f)));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f)));
    }

    /// Calculate the yaw of the given animatable.
    ///
    /// Normally only called for non-[LivingEntities][LivingEntity], and shouldn't be considered a safe place to modify rotation
    /// Do that in [GeoRendererInternals#addRenderData(GeoAnimatable, Object, GeoRenderState, float)] instead
    @Override
    protected float calculateYRot(T animatable, float yHeadRot, float partialTick) {
        return Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) + 180f;
    }
}
