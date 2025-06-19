package anightdazingzoroark.example.entity.hitboxLinker;

import anightdazingzoroark.example.entity.DragonEntity;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.hitboxLogic.EntityHitboxLinker;
import net.minecraft.util.ResourceLocation;

public class DragonHitboxLinker extends EntityHitboxLinker<DragonEntity> {
    @Override
    public ResourceLocation getModelLocation(DragonEntity object) {
        return new ResourceLocation(RiftLib.ModID, "geo/dragon.geo.json");
    }

    @Override
    public ResourceLocation getHitboxFileLocation(DragonEntity entity) {
        return new ResourceLocation(RiftLib.ModID, "hitboxes/dragon.json");
    }
}
