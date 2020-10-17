package software.bernie.example.client.renderer.model.armor;

import net.minecraft.util.Identifier;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class PotatoArmorModel extends AnimatedGeoModel<PotatoArmorItem> {
    @Override
    public Identifier getModelLocation(PotatoArmorItem object) {
        return new Identifier(GeckoLib.ModID, "geo/potato_armor.geo.json");
    }

    @Override
    public Identifier getTextureLocation(PotatoArmorItem object) {
        return new Identifier(GeckoLib.ModID, "textures/item/potato_armor.png");
    }

    @Override
    public Identifier getAnimationFileLocation(PotatoArmorItem animatable) {
        return new Identifier(GeckoLib.ModID, "animations/potato_armor.animation.json");
    }
}
