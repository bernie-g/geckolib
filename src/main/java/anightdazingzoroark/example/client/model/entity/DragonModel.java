package anightdazingzoroark.example.client.model.entity;

import anightdazingzoroark.example.entity.DragonEntity;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import net.minecraft.util.ResourceLocation;

public class DragonModel extends AnimatedGeoModel<DragonEntity> {
    @Override
    public ResourceLocation getModelLocation(DragonEntity object) {
        return new ResourceLocation(RiftLib.ModID, "geo/dragon.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(DragonEntity object) {
        return new ResourceLocation(RiftLib.ModID, "textures/model/entity/dragon.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(DragonEntity animatable) {
        return new ResourceLocation(RiftLib.ModID, "animations/dragon.animation.json");
    }
}
