package software.bernie.example.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import software.bernie.example.block.GeckoHabitatBlock;
import software.bernie.example.block.FertilizerBlock;
import software.bernie.geckolib.GeckoLib;

public class BlockRegistry {

    public static final GeckoHabitatBlock GECKO_HABITAT_BLOCK = registerBlock("gecko_habitat", new GeckoHabitatBlock());
    public static final FertilizerBlock FERTILIZER_BLOCK = registerBlock("fertilizer", new FertilizerBlock());

    public static <B extends Block> B registerBlock(String name, B block) {
        return register(block, new ResourceLocation(GeckoLib.MOD_ID, name));
    }

    private static <B extends Block> B register(B block, ResourceLocation name) {
        Registry.register(BuiltInRegistries.BLOCK, name, block);
        BlockItem item = new BlockItem(block, (new Item.Properties()));

        item.registerBlocks(Item.BY_BLOCK, item);
        Registry.register(BuiltInRegistries.ITEM, name, item);
        return block;
    }
}
