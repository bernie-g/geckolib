package software.bernie.geckolib.example.registry;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.GeckoLib;

public class ItemRegistry
{
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, GeckoLib.ModID);

	public static final RegistryObject<BlockItem> JACK_IN_THE_BOX_ITEM = ITEMS.register("jackintheboxitem", () -> new BlockItem(BlockRegistry.JACK_IN_THE_BOX.get(), new Item.Properties().setISTER(
			() ->
	)));
}
