package software.bernie.example.client.renderer.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class ExampleGeoModel extends AnimatedGeoModel<GeoExampleEntity> {
    @Override
    public Identifier getAnimationFileLocation(GeoExampleEntity entity) {
        return new Identifier(GeckoLib.ModID, "animations/botarium.animation.json");
    }

    @Override
    public Identifier getModelLocation(GeoExampleEntity entity) {
        return new Identifier(GeckoLib.ModID, "geo/geotestmodel.json");
    }

    @Override
    public Identifier getTextureLocation(GeoExampleEntity entity) {
        return new Identifier(GeckoLib.ModID, "textures/model/entity/botarium.png");
    }
}
