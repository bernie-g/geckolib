package software.bernie.geckolib.renderer.specialty;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Specialty class for rendering directionally oriented projectiles.
 * <p>
 * Automatically handles transforms for the entity based on its current rotation.
 * <p>
 * <b><u>NOTE:</u></b> This renderer assumes your model is laying flat, pointing directly north
 */
public class DirectionalProjectileRenderer<T extends Projectile & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public DirectionalProjectileRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    /**
     * Applies rotation transformations to the renderer prior to render time to account for various entity states
     */
    @Override
    protected void applyRotations(R renderState, PoseStack poseStack, float nativeScale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.getGeckolibData(DataTickets.ENTITY_YAW)));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.getGeckolibData(DataTickets.ENTITY_PITCH)));
    }

    /**
     * Calculate the yaw of the given animatable.
     * <p>
     * Normally only called for non-{@link LivingEntity LivingEntities}, and shouldn't be considered a safe place to modify rotation<br>
     * Do that in {@link #addRenderData(GeoAnimatable, Object, GeoRenderState)} instead
     */
    @Override
    protected float calculateYRot(T animatable, float yHeadRot, float partialTick) {
        return Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) + 180f;
    }
}
