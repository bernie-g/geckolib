package software.bernie.geckolib.mixin.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.renderer.GeoEntityRenderState;

/**
 * Duck-typing mixin to apply the {@link GeoEntityRenderState} duck interface to <code>EntityRenderStates</code>
 */
@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements GeoEntityRenderState {
    @Unique
    private float geckolib$partialTick;
    @Unique
    private Entity geckolib$entity;

    @Unique
    @Override
    public void geckolib$setPartialTick(float partialTick) {
        this.geckolib$partialTick = partialTick;
    }

    @Unique
    @Override
    public float geckolib$getPartialTick() {
        return this.geckolib$partialTick;
    }

    @Unique
    @Override
    public void geckolib$setEntity(Entity entity) {
        this.geckolib$entity = entity;
    }

    @Unique
    @Override
    public Entity geckolib$getEntity() {
        return this.geckolib$entity;
    }
}
