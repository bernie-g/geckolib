package software.bernie.example.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.example.block.GeckoHabitatBlock;
import software.bernie.example.block.FertilizerBlock;
import software.bernie.geckolib3.GeckoLib;

public final class BlockRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			GeckoLib.MOD_ID);

	public static final RegistryObject<GeckoHabitatBlock> GECKO_HABITAT = BLOCKS.register("gecko_habitat",
			GeckoHabitatBlock::new);
	public static final RegistryObject<FertilizerBlock> FERTILIZER = BLOCKS.register("fertilizer",
			FertilizerBlock::new);
}
