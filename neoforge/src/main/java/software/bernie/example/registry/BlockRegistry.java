package software.bernie.example.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import software.bernie.example.block.FertilizerBlock;
import software.bernie.example.block.GeckoHabitatBlock;
import software.bernie.geckolib.GeckoLib;

public final class BlockRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK,
			GeckoLib.MOD_ID);

	public static final DeferredHolder<Block, GeckoHabitatBlock> GECKO_HABITAT = BLOCKS.register("gecko_habitat",
			GeckoHabitatBlock::new);
	public static final DeferredHolder<Block, FertilizerBlock> FERTILIZER = BLOCKS.register("fertilizer",
			FertilizerBlock::new);
}
