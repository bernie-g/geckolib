package software.bernie.example.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import software.bernie.example.block.FertilizerBlock;
import software.bernie.example.block.GeckoHabitatBlock;
import software.bernie.geckolib.GeckoLibConstants;

public class BlockRegistry {

    public static final GeckoHabitatBlock GECKO_HABITAT_BLOCK = registerBlock("gecko_habitat", new GeckoHabitatBlock());
    public static final FertilizerBlock BOTARIUM_BLOCK = registerBlock("fertilizer", new FertilizerBlock());

    public static <B extends Block> B registerBlock(String name, B block) {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(GeckoLibConstants.MODID, name), block);
    }
}
