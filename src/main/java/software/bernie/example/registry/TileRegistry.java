package software.bernie.example.registry;

import net.minecraft.block.entity.BlockEntityType;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;

public class TileRegistry {
    public static final BlockEntityType<BotariumTileEntity> BOTARIUM_TILE = RegistryUtils.registerBlockEntity("botariumtile", BlockEntityType.Builder.create(BotariumTileEntity::new, BlockRegistry.BOTARIUM_BLOCK));
    public static final BlockEntityType<FertilizerTileEntity> FERTILIZER = RegistryUtils.registerBlockEntity("fertilizertile", BlockEntityType.Builder.create(FertilizerTileEntity::new, BlockRegistry.FERTILIZER_BLOCK));
}
