package anightdazingzoroark.example.entity.hitboxLinker;

import anightdazingzoroark.example.entity.DragonEntity;
import anightdazingzoroark.example.entity.FlyingPufferfishEntity;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.hitboxLogic.EntityHitboxLinker;
import net.minecraft.util.ResourceLocation;

public class FlyingPufferfishHitboxLinker extends EntityHitboxLinker<FlyingPufferfishEntity> {
    @Override
    public ResourceLocation getModelLocation(FlyingPufferfishEntity object) {
        return new ResourceLocation(RiftLib.ModID, "geo/flying_pufferfish.geo.json");
    }

    @Override
    public ResourceLocation getHitboxFileLocation(FlyingPufferfishEntity entity) {
        return new ResourceLocation(RiftLib.ModID, "hitboxes/flying_pufferfish.json");
    }
}
