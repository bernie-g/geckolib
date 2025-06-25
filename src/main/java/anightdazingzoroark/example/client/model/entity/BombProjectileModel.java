package anightdazingzoroark.example.client.model.entity;

import anightdazingzoroark.example.entity.BombProjectile;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import net.minecraft.util.ResourceLocation;

public class BombProjectileModel extends AnimatedGeoModel<BombProjectile> {
    @Override
    public ResourceLocation getModelLocation(BombProjectile object) {
        return new ResourceLocation(RiftLib.ModID, "geo/bomb.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BombProjectile object) {
        return new ResourceLocation(RiftLib.ModID, "textures/model/entity/bomb.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BombProjectile animatable) {
        return null;
    }
}
