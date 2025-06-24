package anightdazingzoroark.example.entity.ridePosLinker;

import anightdazingzoroark.example.entity.DragonEntity;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.ridePositionLogic.DynamicRidePosLinker;
import net.minecraft.util.ResourceLocation;

public class DragonRidePosLinker extends DynamicRidePosLinker<DragonEntity> {
    @Override
    public ResourceLocation getModelLocation(DragonEntity object) {
        return new ResourceLocation(RiftLib.ModID, "geo/dragon.geo.json");
    }
}
