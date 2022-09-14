package software.bernie.example.registry;

import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.geckolib3q.GeckoLib;

public class TileRegistry {
	public static final BlockEntityType<HabitatTileEntity> HABITAT_TILE = Registry.register(Registry.BLOCK_ENTITY_TYPE,
			GeckoLib.ModID + ":habitattile",
			QuiltBlockEntityTypeBuilder.create(HabitatTileEntity::new, BlockRegistry.HABITAT_BLOCK).build(null));
	public static final BlockEntityType<FertilizerTileEntity> FERTILIZER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
			GeckoLib.ModID + ":fertilizertile",
			QuiltBlockEntityTypeBuilder.create(FertilizerTileEntity::new, BlockRegistry.FERTILIZER_BLOCK).build(null));
}
