package anightdazingzoroark.example.client.model.item;

import anightdazingzoroark.example.item.BombItem;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import net.minecraft.util.ResourceLocation;

public class BombModel extends AnimatedGeoModel<BombItem> {
    @Override
    public ResourceLocation getModelLocation(BombItem object) {
        return new ResourceLocation(RiftLib.ModID, "geo/bomb.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BombItem object) {
        return new ResourceLocation(RiftLib.ModID, "textures/model/entity/bomb.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BombItem animatable) {
        return null;
    }
}
