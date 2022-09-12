package software.bernie.example.registry;

import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.geckolib3q.GeckoLib;

public class TileRegistry {
	public static final BlockEntityType<BotariumTileEntity> BOTARIUM_TILE = Registry.register(
			Registry.BLOCK_ENTITY_TYPE, GeckoLib.ModID + ":botariumtile",
			QuiltBlockEntityTypeBuilder.create(BotariumTileEntity::new, BlockRegistry.BOTARIUM_BLOCK).build(null));
	public static final BlockEntityType<FertilizerTileEntity> FERTILIZER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
			GeckoLib.ModID + ":fertilizertile",
			QuiltBlockEntityTypeBuilder.create(FertilizerTileEntity::new, BlockRegistry.FERTILIZER_BLOCK).build(null));
}
