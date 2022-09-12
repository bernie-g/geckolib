package software.bernie.example.registry;

import software.bernie.example.block.HabitatBlock;
import software.bernie.example.block.FertilizerBlock;

public class BlockRegistry {
	public static final HabitatBlock HABITAT_BLOCK = RegistryUtils.register("habitatblock", new HabitatBlock());
	public static final FertilizerBlock FERTILIZER_BLOCK = RegistryUtils.register("fertilizerblock",
			new FertilizerBlock());
}
