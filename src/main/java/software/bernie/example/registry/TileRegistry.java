package software.bernie.example.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.GeckoLib;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.block.tile.TileEntityJackInTheBox;

public class TileRegistry
{
	public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, GeckoLib.ModID);

	public static final RegistryObject<TileEntityType<TileEntityJackInTheBox>> JACK_IN_THE_BOX_TILE = TILES.register("jackintheboxtile", () -> TileEntityType.Builder.create(TileEntityJackInTheBox::new, BlockRegistry.BOTARIUM_BLOCK.get()).build(null));
	public static final RegistryObject<TileEntityType<BotariumTileEntity>> BOTARIUM_TILE = TILES.register("botariumtile", () -> TileEntityType.Builder.create(BotariumTileEntity::new, BlockRegistry.BOTARIUM_BLOCK.get()).build(null));
	public static final RegistryObject<TileEntityType<FertilizerTileEntity>> FERTILIZER = TILES.register("fertilizertile", () -> TileEntityType.Builder.create(FertilizerTileEntity::new, BlockRegistry.FERTILIZER_BLOCK.get()).build(null));
}
