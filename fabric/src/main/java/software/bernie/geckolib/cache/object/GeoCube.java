package software.bernie.geckolib.cache.object;

import net.minecraft.world.phys.Vec3;

/**
 * Baked cuboid for a {@link GeoBone}
 */
public record GeoCube(GeoQuad[] quads, Vec3 pivot, Vec3 rotation, Vec3 size, double inflate, boolean mirror) {}
