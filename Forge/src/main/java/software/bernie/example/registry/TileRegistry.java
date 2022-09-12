package software.bernie.example.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.geckolib3.GeckoLib;

public class TileRegistry {
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITIES, GeckoLib.ModID);

	public static final RegistryObject<BlockEntityType<BotariumTileEntity>> BOTARIUM_TILE = TILES
			.register("botariumtile", () -> BlockEntityType.Builder
					.of(BotariumTileEntity::new, BlockRegistry.BOTARIUM_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<FertilizerTileEntity>> FERTILIZER = TILES
			.register("fertilizertile", () -> BlockEntityType.Builder
					.of(FertilizerTileEntity::new, BlockRegistry.FERTILIZER_BLOCK.get()).build(null));
}
