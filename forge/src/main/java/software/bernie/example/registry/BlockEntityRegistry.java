package software.bernie.example.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.example.block.entity.FertilizerBlockEntity;
import software.bernie.example.block.entity.GeckoHabitatBlockEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.GeckoLibConstants;

public final class BlockEntityRegistry {
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GeckoLibConstants.MODID);

	public static final RegistryObject<BlockEntityType<GeckoHabitatBlockEntity>> GECKO_HABITAT = TILES
			.register("habitat", () -> BlockEntityType.Builder
					.of(GeckoHabitatBlockEntity::new, BlockRegistry.GECKO_HABITAT.get()).build(null));
	public static final RegistryObject<BlockEntityType<FertilizerBlockEntity>> FERTILIZER_BLOCK = TILES
			.register("fertilizer", () -> BlockEntityType.Builder
					.of(FertilizerBlockEntity::new, BlockRegistry.FERTILIZER.get()).build(null));
}
