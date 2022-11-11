package software.bernie.example.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.example.block.entity.HabitatBlockEntity;
import software.bernie.example.block.entity.FertilizerBlockEntity;
import software.bernie.geckolib3.GeckoLib;

public final class TileRegistry {
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GeckoLib.MOD_ID);

	public static final RegistryObject<BlockEntityType<HabitatBlockEntity>> HABITAT_TILE = TILES
			.register("habitat", () -> BlockEntityType.Builder
					.of(HabitatBlockEntity::new, BlockRegistry.HABITAT_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<FertilizerBlockEntity>> FERTILIZER = TILES
			.register("fertilizer", () -> BlockEntityType.Builder
					.of(FertilizerBlockEntity::new, BlockRegistry.FERTILIZER_BLOCK.get()).build(null));
}
