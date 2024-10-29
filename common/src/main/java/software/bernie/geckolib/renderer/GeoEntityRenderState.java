package software.bernie.geckolib.renderer;

import net.minecraft.world.entity.Entity;

/**
 * Duck interface to bypass vanilla's stripping of the entity from rendering.
 * <p>
 * For the time being, this is inconsequential; as the entity is fully present during rendering.
 * This may change in the future with further vanilla changes, which will necessitate changes to this approach
 */
public interface GeoEntityRenderState {
    void geckolib$setPartialTick(float partialTick);
    float geckolib$getPartialTick();
    void geckolib$setEntity(Entity entity);
    Entity geckolib$getEntity();
}
