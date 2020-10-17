/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.GeckoLib;

public class GeckoLibMod implements ModInitializer {
    @Override
    public void onInitialize() {
        GeckoLib.initialize();
        new EntityRegistry();
        FabricDefaultAttributeRegistry.register(EntityRegistry.GEO_EXAMPLE_ENTITY, EntityUtils.createGenericEntityAttributes());
        new ItemRegistry();
        new TileRegistry();
        new BlockRegistry();
    }
}
