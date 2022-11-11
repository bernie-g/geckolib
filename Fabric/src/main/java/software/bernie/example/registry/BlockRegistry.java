package software.bernie.example.registry;

import software.bernie.example.block.FertilizerBlock;
import software.bernie.example.block.HabitatBlock;

public class BlockRegistry {
	public static final HabitatBlock HABITAT_BLOCK = RegistryUtils.register("habitatblock", new HabitatBlock());
	public static final FertilizerBlock FERTILIZER_BLOCK = RegistryUtils.register("fertilizerblock",
			new FertilizerBlock());
}
