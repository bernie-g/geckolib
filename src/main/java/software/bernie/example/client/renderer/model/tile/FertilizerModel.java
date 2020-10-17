// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLibMod
// Paste this class into your mod and follow the documentation for GeckoLibMod to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.renderer.model.tile;

import net.minecraft.util.Identifier;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class FertilizerModel extends AnimatedGeoModel<FertilizerTileEntity> {
    @Override
    public Identifier getAnimationFileLocation(FertilizerTileEntity animatable) {
        if (animatable.getWorld().isRaining()) {
            return new Identifier("geckolib", "animations/fertilizer.animation.json");
        } else {
            return new Identifier("geckolib", "animations/botarium.animation.json");
        }
    }

    @Override
    public Identifier getModelLocation(FertilizerTileEntity animatable) {
        if (animatable.getWorld().isRaining()) {
            return new Identifier("geckolib", "geo/fertilizer.geo.json");
        } else {
            return new Identifier("geckolib", "geo/botarium.geo.json");
        }
    }

    @Override
    public Identifier getTextureLocation(FertilizerTileEntity entity) {
        if (entity.getWorld().isRaining()) {
            return new Identifier("geckolib" + ":textures/block/fertilizer.png");
        } else {
            return new Identifier("geckolib" + ":textures/block/botarium.png");
        }
    }
}