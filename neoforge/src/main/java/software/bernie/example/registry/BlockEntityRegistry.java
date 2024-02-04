package software.bernie.example.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import software.bernie.example.block.entity.FertilizerBlockEntity;
import software.bernie.example.block.entity.GeckoHabitatBlockEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.GeckoLibConstants;

public final class BlockEntityRegistry {
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister
			.create(Registries.BLOCK_ENTITY_TYPE, GeckoLibConstants.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeckoHabitatBlockEntity>> GECKO_HABITAT = TILES
			.register("habitat", () -> BlockEntityType.Builder
					.of(GeckoHabitatBlockEntity::new, BlockRegistry.GECKO_HABITAT.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FertilizerBlockEntity>> FERTILIZER_BLOCK = TILES
			.register("fertilizer", () -> BlockEntityType.Builder
					.of(FertilizerBlockEntity::new, BlockRegistry.FERTILIZER.get()).build(null));
}
