package software.bernie.example.registry;

import net.minecraft.block.entity.BlockEntityType;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.block.tile.HabitatTileEntity;

public class TileRegistry {
	public static final BlockEntityType<HabitatTileEntity> HABITAT_TILE = RegistryUtils.registerBlockEntity(
			"habitattile", BlockEntityType.Builder.create(HabitatTileEntity::new, BlockRegistry.HABITAT_BLOCK));
	public static final BlockEntityType<FertilizerTileEntity> FERTILIZER = RegistryUtils.registerBlockEntity(
			"fertilizertile",
			BlockEntityType.Builder.create(FertilizerTileEntity::new, BlockRegistry.FERTILIZER_BLOCK));
}
