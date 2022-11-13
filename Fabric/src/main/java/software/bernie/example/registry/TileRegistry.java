package software.bernie.example.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.geckolib3.GeckoLib;

public class TileRegistry {
	public static final BlockEntityType<HabitatTileEntity> HABITAT_TILE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			GeckoLib.ModID + ":habitattile",
			FabricBlockEntityTypeBuilder.create(HabitatTileEntity::new, BlockRegistry.HABITAT_BLOCK).build(null));
	public static final BlockEntityType<FertilizerTileEntity> FERTILIZER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			GeckoLib.ModID + ":fertilizertile",
			FabricBlockEntityTypeBuilder.create(FertilizerTileEntity::new, BlockRegistry.FERTILIZER_BLOCK).build(null));
}
