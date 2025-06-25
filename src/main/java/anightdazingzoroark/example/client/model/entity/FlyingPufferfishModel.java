package anightdazingzoroark.example.client.model.entity;

import anightdazingzoroark.example.entity.FlyingPufferfishEntity;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import net.minecraft.util.ResourceLocation;

public class FlyingPufferfishModel extends AnimatedGeoModel<FlyingPufferfishEntity> {
    @Override
    public ResourceLocation getModelLocation(FlyingPufferfishEntity object) {
        return new ResourceLocation(RiftLib.ModID, "geo/flying_pufferfish.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(FlyingPufferfishEntity object) {
        return new ResourceLocation(RiftLib.ModID, "textures/model/entity/flying_pufferfish.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(FlyingPufferfishEntity animatable) {
        return new ResourceLocation(RiftLib.ModID, "animations/flying_pufferfish.animation.json");
    }
}
