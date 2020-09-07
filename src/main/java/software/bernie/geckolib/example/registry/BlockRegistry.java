package software.bernie.geckolib.example.registry;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.block.BotariumBlock;
import software.bernie.geckolib.example.block.FertilizerBlock;
import software.bernie.geckolib.example.block.JackInTheBoxBlock;

public class BlockRegistry
{
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, GeckoLib.ModID);

	public static final RegistryObject<JackInTheBoxBlock> JACK_IN_THE_BOX = BLOCKS.register("jackintheboxblock", JackInTheBoxBlock::new);
	public static final RegistryObject<BotariumBlock> BOTARIUM_BLOCK = BLOCKS.register("botariumblock", BotariumBlock::new);
	public static final RegistryObject<FertilizerBlock> FERTILIZER_BLOCK = BLOCKS.register("fertilizerblock", FertilizerBlock::new);
}
