package anightdazingzoroark.example.client.model.entity;

import anightdazingzoroark.example.entity.DragonEntity;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedMultiHitboxGeoModel;
import net.minecraft.util.ResourceLocation;

public class DragonModel extends AnimatedMultiHitboxGeoModel<DragonEntity> {
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

    @Override
    public ResourceLocation getHitboxFileLocation(DragonEntity entity) {
        return new ResourceLocation(RiftLib.ModID, "hitboxes/dragon.json");
    }
}
