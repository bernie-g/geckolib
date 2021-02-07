package software.bernie.example.registry;

import software.bernie.example.block.BotariumBlock;
import software.bernie.example.block.FertilizerBlock;

public class BlockRegistry {
	public static final BotariumBlock BOTARIUM_BLOCK = RegistryUtils.register("botariumblock", new BotariumBlock());
	public static final FertilizerBlock FERTILIZER_BLOCK = RegistryUtils.register("fertilizerblock",
			new FertilizerBlock());
}
