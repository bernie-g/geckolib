/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.client.render.RenderLayer;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib.renderer.geo.GeoArmorRenderer;
import software.bernie.geckolib.renderer.geo.GeoItemRenderer;

public class ClientListener implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.INSTANCE.register(EntityRegistry.GEO_EXAMPLE_ENTITY, (entityRenderDispatcher, context) -> new ExampleGeoRenderer(entityRenderDispatcher));
        GeoItemRenderer.registerItemRenderer(ItemRegistry.JACK_IN_THE_BOX, new JackInTheBoxRenderer());
        GeoArmorRenderer.registerArmorRenderer(PotatoArmorItem.class, new PotatoArmorRenderer());
        BlockEntityRendererRegistry.INSTANCE.register(TileRegistry.BOTARIUM_TILE, BotariumTileRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(TileRegistry.FERTILIZER, FertilizerTileRenderer::new);

        BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.BOTARIUM_BLOCK, RenderLayer.getCutout());

    }
}
