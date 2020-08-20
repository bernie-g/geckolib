package software.bernie.geckolib.example.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.block.tile.TileEntityJackInTheBox;

public class TileRegistry
{
	public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, GeckoLib.ModID);

	public static final RegistryObject<TileEntityType<TileEntityJackInTheBox>> JACK_IN_THE_BOX_TILE = TILES.register("jackintheboxtile", () -> TileEntityType.Builder.create(TileEntityJackInTheBox::new, BlockRegistry.JACK_IN_THE_BOX.get()).build(null));
}
